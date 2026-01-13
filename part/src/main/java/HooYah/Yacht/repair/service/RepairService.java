package HooYah.Yacht.repair.service;

import HooYah.Redis.CacheService;
import HooYah.Yacht.excetion.CustomException;
import HooYah.Yacht.excetion.ErrorCode;
import HooYah.Yacht.part.domain.Part;
import HooYah.Yacht.part.repository.PartRepository;
import HooYah.Yacht.repair.domain.Repair;
import HooYah.Yacht.repair.repository.RepairRepository;
import HooYah.Yacht.webclient.WebClient;
import HooYah.Yacht.webclient.WebClient.HttpMethod;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RepairService {

    private final RepairRepository repairRepository;
    private final PartRepository partRepository;

    private final CacheService yachtCacheService;
    private final WebClient webClient;

    @Value("${web-client.gateway}")
    private String gatewayURL;

    @Value("${web-client.yacht}")
    private String yachtURI;
    @Value("${web-client.yacht-user}")
    private String yachtUserURI;
    @Value("${web-client.calendar-alarm-auto-generate}")
    private String calendarAlarmAutoGenerateURI;

    @Transactional
    public List<Repair> getRepairListByPart(
            Long partId, Long userId
    ) {
        Part part = partRepository.findById(partId).orElseThrow(
                ()-> new CustomException(ErrorCode.NOT_FOUND)
        );

        // validate User in Yacht
        validateYachtUser(part.getYachtId(), userId);

        return repairRepository.findRepairListByPart(partId);
    }

    @Transactional
    public void addRepair(Long partId, String content, OffsetDateTime repairDate, Long userId) {
        Part part = partRepository.findById(partId).orElseThrow(
                ()-> new CustomException(ErrorCode.NOT_FOUND)
        );

        validateYachtUser(part.getYachtId(), userId);

        Repair repair = Repair
                .builder()
                .part(part)
                .userId(userId)
                .repairDate(repairDate)
                .content(content)
                .build();

        repairRepository.save(repair);

        updateCalenderAndAlarm(part);
    }

    @Transactional
    public Repair updateRepair(Long repairId, String content, OffsetDateTime updateDate, Long userId) {
        Repair repair = repairRepository.findById(repairId).orElseThrow(
                ()->new CustomException(ErrorCode.NOT_FOUND)
        );
        Part part = repair.getPart();

        validateYachtUser(part.getYachtId(), userId);

        repair.updateContent(content);

        if(updateDate != null) {
            repair.updateRepairDate(updateDate);
            updateCalenderAndAlarm(part);
        }

        return repair;
    }

    @Transactional
    public void deleteRepair(Long repairId, Long userId) {
        Repair repair = repairRepository.findById(repairId).orElseThrow(
                ()->new CustomException(ErrorCode.NOT_FOUND)
        );
        Part part = repair.getPart();

        validateYachtUser(part.getYachtId(), userId);

        repairRepository.delete(repair);
    }

    private void validateYachtUser(Long yachtId, Long userId) {
        String uri = String.format(gatewayURL + yachtUserURI, yachtId, userId);

        Object yachtUser = yachtCacheService.getOrSelect(
                yachtId, userId,
                ()-> webClient.webClient(uri, HttpMethod.GET, null)
        );

        if(yachtUser == null)
            throw new CustomException(ErrorCode.CONFLICT);
    }

    private void updateCalenderAndAlarm(Part part) {
        Optional<Repair> lastRepairOpt = repairRepository.findByIdOrderByRepairDateDesc(part.getId());

        if(lastRepairOpt.isEmpty())
            return;

        Map<String, Object> body = Map.of(
                "partId", part.getId(),
                "yachtId", part.getYachtId(),
                "nextRepairDate", lastRepairOpt.get().getRepairDate()
        );

        webClient.webClient(
                gatewayURL + calendarAlarmAutoGenerateURI,
                HttpMethod.POST,
                body
        );
    }

}
