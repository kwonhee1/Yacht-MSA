package HooYah.Yacht.service;

import HooYah.Redis.CacheService;
import HooYah.Yacht.domain.Part;
import HooYah.Yacht.domain.Repair;
import HooYah.Yacht.dto.part.AddPartDto;
import HooYah.Yacht.dto.part.UpdatePartDto;
import HooYah.Yacht.dto.part.PartDto;
import HooYah.Yacht.excetion.CustomException;
import HooYah.Yacht.excetion.ErrorCode;
import HooYah.Yacht.repository.PartRepository;
import HooYah.Yacht.repository.RepairRepository;
import HooYah.Yacht.webclient.WebClient;
import HooYah.Yacht.webclient.WebClient.HttpMethod;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.transaction.support.TransactionTemplate;

@Service
@RequiredArgsConstructor
public class PartService {

    private final PartRepository partRepository;
    private final RepairRepository repairRepository;
    private final UpdateCalendarAndAlarmService updateCalendarAndAlarmService;

    private final RepairService repairService;

    private final TransactionTemplate transactionTemplate;

    private final CacheService yachtCacheService;
    private final WebClient webClient;

    @Value("${web-client.gateway}")
    private String gatewayURL;

    @Value("${web-client.yacht-user}")
    private String yachtUserURI;

    public Part addPart(AddPartDto dto, Long userId) {
        validateYachtUser(dto.getYachtId(), userId);

        Part newPart = partRepository.save(Part
                .builder()
                .yachtId(dto.getYachtId())
                .name(dto.getName())
                .manufacturer(dto.getManufacturer())
                .model(dto.getModel())
                .interval(dto.getInterval())
                .build()
        );

        if(dto.getLastRepair() != null)
            repairService.addRepair(newPart, "Auto Generated", dto.getLastRepair(), userId);

        return newPart;
    }

    public List<Part> addPartList(Long yachtId, List<AddPartDto> dtoList) {
        List<Part> createdPartList = dtoList
                    .stream()
                    .map((dto) -> Part
                        .builder()
                        .yachtId(yachtId)
                        .name(dto.getName())
                        .manufacturer(dto.getManufacturer())
                        .model(dto.getModel())
                        .interval(dto.getInterval())
                        .build()
                    )
                    .toList();

        partRepository.saveAll(createdPartList); // only one query, not need transaction

        requestAddDefaultRepair(createdPartList, dtoList);

        return createdPartList;
    }

    private void requestAddDefaultRepair(List<Part> createdPartList, List<AddPartDto> dtoList) {
        // filter dto.getListRepair != null
        List<Part> repairAddPartList = new ArrayList<>();
        List<OffsetDateTime> repairAddRepairList = new ArrayList<>();

        for(int i = 0; i < createdPartList.size(); i++) {
            if(dtoList.get(i).getLastRepair() != null) { // 이거 생각보다 위험하다 :: repair에 필요한 값이 추가 된다면 Part값이 수정되어야함 --> 다른 domain의 값이 dto에 침범함
                OffsetDateTime repairDate = dtoList.get(i).getLastRepair();

                repairAddRepairList.add(repairDate);
                repairAddPartList.add(createdPartList.get(i));
            }
        }

        repairService.addRepairList(repairAddPartList, repairAddRepairList);
    }

    public List<PartDto> getPartListByYacht(Long yachtId, Long userId) {
        validateYachtUser(yachtId, userId);

        List<PartDto> result = transactionTemplate.execute((status)->{
            List<Part> partList = partRepository.findPartListByYacht(yachtId);
            List<Repair> lastRepairList = repairRepository.findAllLastRepair(partList.stream().map(Part::getId).toList());
            return toPartDtoList(partList, lastRepairList);
        });

        return result;
    }

    @Transactional
    public List<PartDto> getPartListByIdList(List<Long> partIdList) {
        List<Part> partList = partRepository.findAllById(partIdList);
        List<Repair> lastRepairList = repairRepository.findAllLastRepair(partList.stream().map(Part::getId).toList());
        return toPartDtoList(partList, lastRepairList);
    }

    /*
        generate Part Dto List
            List<PartDot.of(part, LastRepair)>

            @Param partList : partList
            @Param mixedLastRepairList : 순서가 보장되지 않는 LastRepairList
     */
    private List<PartDto> toPartDtoList(
            List<Part> partList,
            List<Repair> mixedLastRepairList
    ) {
        Map<Long, Repair> lastRepairMap = mixedLastRepairList.stream().collect(Collectors.toMap(
                repair -> repair.getPart().getId(),
                repair -> repair
        ));

        return partList.stream().map(part -> PartDto.of(part, lastRepairMap.get(part.getId()))).toList();
    }

    public void updatePart(UpdatePartDto dto, Long userId) {
        Part part = partRepository.findById(dto.getId()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND)
        );

        validateYachtUser(part.getYachtId(), userId);

        part.update(dto.getName(), dto.getManufacturer(), dto.getModel());
        boolean isChangedInterval = part.updateInterval(dto.getInterval());

        partRepository.save(part);

        if(isChangedInterval)
            updateCalendarAndAlarmService.updateCalendarAndAlarm(part);
    }

    public void deletePart(Long id, Long userId) {
        Part part = partRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND)
        );

        validateYachtUser(part.getYachtId(), userId);

        // delete other
        transactionTemplate.executeWithoutResult((status)->partRepository.delete(part));
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
