package HooYah.Yacht.repair.service;

import HooYah.Redis.RedisService;
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
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RepairService {

    private final RepairRepository repairRepository;
    private final PartRepository partRepository;

    private final RedisService yachtRedisService;
    private final WebClient webClient;

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

        // updateCalenderAndAlarm(part); part가 생성되었음을 알림!
    }

    @Transactional
    public void updateRepair(Long repairId, String content, OffsetDateTime updateDate, Long userId) {
        Repair repair = repairRepository.findById(repairId).orElseThrow(
                ()->new CustomException(ErrorCode.NOT_FOUND)
        );
        Part part = repair.getPart();

        validateYachtUser(part.getYachtId(), userId);

        repair.updateRepairDate(updateDate);
        repair.updateContent(content);

        // updateCalenderAndAlarm(part);
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
        String uri = String.format("", yachtId, userId);

        Optional yachtUser = yachtRedisService.getOrSelect(
                yachtId, userId,
                ()-> Optional.of(webClient.webClient(uri, HttpMethod.GET, null))
        );

        if(yachtUser.isEmpty())
            throw new CustomException(ErrorCode.CONFLICT);
    }

}
