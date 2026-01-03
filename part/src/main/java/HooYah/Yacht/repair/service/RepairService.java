package HooYah.Yacht.repair.service;

import HooYah.Yacht.common.excetion.CustomException;
import HooYah.Yacht.common.excetion.ErrorCode;
import HooYah.Yacht.part.domain.Part;
import HooYah.Yacht.part.repository.PartRepository;
import HooYah.Yacht.repair.domain.Repair;
import HooYah.Yacht.repair.repository.RepairRepository;
import HooYah.Yacht.redis.YachtService;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RepairService {

    private final RepairRepository repairRepository;
    private final PartRepository partRepository;

    private final YachtService yachtRedisService;

    @Transactional
    public List<Repair> getRepairListByPart(
            Long partId, Long userId
    ) {
        Part part = partRepository.findById(partId).orElseThrow(
                ()-> new CustomException(ErrorCode.NOT_FOUND)
        );

        // validate User in Yacht
        yachtRedisService.validateYachtUser(part.getYachtId(), userId);

        return repairRepository.findRepairListByPart(partId);
    }

    @Transactional
    public void addRepair(Long partId, String content, OffsetDateTime repairDate, Long userId) {
        Part part = partRepository.findById(partId).orElseThrow(
                ()-> new CustomException(ErrorCode.NOT_FOUND)
        );

        yachtRedisService.validateYachtUser(part.getYachtId(), userId);

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

        yachtRedisService.validateYachtUser(part.getYachtId(), userId);

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

        yachtRedisService.validateYachtUser(part.getYachtId(), userId);

        repairRepository.delete(repair);
    }

}
