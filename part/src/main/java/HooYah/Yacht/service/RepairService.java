package HooYah.Yacht.service;

import HooYah.Redis.CacheService;
import HooYah.Yacht.domain.Part;
import HooYah.Yacht.domain.Repair;
import HooYah.Yacht.excetion.CustomException;
import HooYah.Yacht.excetion.ErrorCode;
import HooYah.Yacht.repository.PartRepository;
import HooYah.Yacht.repository.RepairRepository;
import HooYah.Yacht.webclient.WebClient;
import HooYah.Yacht.webclient.WebClient.HttpMethod;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.transaction.support.TransactionTemplate;

@Service
@RequiredArgsConstructor
public class RepairService {

    private final RepairRepository repairRepository;
    private final PartRepository partRepository;
    private final UpdateCalendarAndAlarmService updateCalendarAndAlarmService;

    private final CacheService yachtCacheService;
    private final WebClient webClient;

    private final TransactionTemplate transactionTemplate;

    @Value("${web-client.gateway}")
    private String gatewayURL;
    @Value("${web-client.yacht-user}")
    private String yachtUserURI;

    public List<Repair> getRepairListByPart(
            Long partId, Long userId
    ) {
        Part part = partRepository.findById(partId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND)
        );

        // validate User in Yacht
        validateYachtUser(part.getYachtId(), userId);

        return repairRepository.findRepairListByPart(partId);
    }

    public Repair addRepair(Long partId, String content, OffsetDateTime repairDate, Long userId) {
        Part part = partRepository.findById(partId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND)
        );
        validateYachtUser(part.getYachtId(), userId);
        return addRepair(part, content, repairDate, userId);
    }

    public Repair addRepair(Part part, String content, OffsetDateTime repairDate, Long userId) {
        Repair newRepair = repairRepository.save( // only one query, not need transaction
                Repair
                        .builder()
                        .part(part)
                        .userId(userId)
                        .repairDate(repairDate)
                        .content(content)
                        .build()
        );

        updateCalendarAndAlarmService.updateCalendarAndAlarm(part);

        return newRepair;
    }

    // used from addPartList (proxy api)
    public void addRepairList(List<Part> partList, List<OffsetDateTime> repairDateList) {
        List<Repair> repairList = new ArrayList<>();

        for (int i = 0; i < repairDateList.size(); i++) {
            repairList.add(Repair
                    .builder()
                    .part(partList.get(i))
                    .repairDate(repairDateList.get(i))
                    .content("auto generated")
                    .build()
            );
        }

        repairRepository.saveAll(repairList);
    }

    public Repair updateRepair(Long repairId, String content, OffsetDateTime updateDate, Long userId) {
        Repair repair = repairRepository.findById(repairId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND)
        );
        Part part = repair.getPart();

        validateYachtUser(part.getYachtId(), userId);

        repair.updateContent(content);
        boolean isUpdateRepairDate = repair.updateRepairDate(updateDate);

        repairRepository.save(repair);

        if (isUpdateRepairDate)
            updateCalendarAndAlarmService.updateCalendarAndAlarm(part);

        return repair;
    }

    public void deleteRepair(Long repairId, Long userId) {
        Repair repair = repairRepository.findById(repairId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND)
        );
        Part part = repair.getPart();

        validateYachtUser(part.getYachtId(), userId);

        transactionTemplate.executeWithoutResult((status)->
                repairRepository.delete(repair)
        );
    }

    private void validateYachtUser(Long yachtId, Long userId) {
        String uri = String.format(gatewayURL + yachtUserURI, yachtId, userId);

        Object yachtUser = yachtCacheService.getOrSelect(
                yachtId, userId,
                () -> webClient.webClient(uri, HttpMethod.GET, null).toMap()
        );

        if (yachtUser == null) {
            throw new CustomException(ErrorCode.CONFLICT);
        }
    }

}
