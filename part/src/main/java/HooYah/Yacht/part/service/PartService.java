package HooYah.Yacht.part.service;

import HooYah.Yacht.common.excetion.CustomException;
import HooYah.Yacht.common.excetion.ErrorCode;
import HooYah.Yacht.part.domain.Part;
import HooYah.Yacht.repair.domain.Repair;
import HooYah.Yacht.part.dto.request.AddPartDto;
import HooYah.Yacht.part.dto.response.PartDto;
import HooYah.Yacht.part.dto.request.UpdatePartDto;
import HooYah.Yacht.part.repository.PartRepository;
import HooYah.Yacht.repair.repository.RepairRepository;
import HooYah.Yacht.repair.service.RepairService;
import HooYah.Yacht.redis.YachtService;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PartService {

    private final RepairService repairService;

    private final PartRepository partRepository;
    private final RepairRepository repairRepository;

    private final YachtService yachtRedisService;

    public List<PartDto> getPartListByYacht(Long yachtId, Long userId) {
        yachtRedisService.validateYachtUser(yachtId, userId);

        List<Part> partList = partRepository.findPartListByYacht(yachtId);
        List<Repair> lastRepairList = repairRepository.findAllLastRepair(partList.stream().map(Part::getId).toList());

        Map<Long, Repair> lastRepairMap = lastRepairList.stream().collect(Collectors.toMap(
                repair -> repair.getPart().getId(),
                repair -> repair
        ));

        return partList.stream().map(part -> PartDto.of(part, lastRepairMap.get(part.getId()))).toList();
    }

    @Transactional
    public Part addPart(Long yachtId, AddPartDto dto, Long userId) {
        yachtRedisService.validateYacht(yachtId);

        Part newPart = Part
                .builder()
                .yachtId(yachtId)
                .name(dto.getName())
                .manufacturer(dto.getManufacturer())
                .model(dto.getModel())
                .interval(dto.getInterval())
                .build();
        newPart = partRepository.save(newPart);

        if(dto.getLastRepair() != null)
            repairService.addRepair(newPart.getId(), null, dto.getLastRepair(), userId);

        return newPart;
    }

    @Transactional
    public void updatePart(UpdatePartDto dto, Long userId) {
        Part part = partRepository.findById(dto.getId()).orElseThrow(
                ()-> new CustomException(ErrorCode.NOT_FOUND)
        );

        yachtRedisService.validateYachtUser(part.getYachtId(), userId);

        part.update(dto.getName(), dto.getManufacturer(), dto.getModel());

        if(dto.getInterval() != null) {
            part.updateInterval(dto.getInterval());
            // calendarAlarmAutoGeneratorService.generate(part);
        }
    }

    @Transactional
    public void deletePart(Long id, Long userId) {
        Part part = partRepository.findById(id).orElseThrow(
                ()-> new CustomException(ErrorCode.NOT_FOUND)
        );

        yachtRedisService.validateYachtUser(part.getYachtId(), userId);

        // delete other

        partRepository.delete(part);
    }

}
