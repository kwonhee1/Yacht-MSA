package HooYah.Yacht.service;

import HooYah.Redis.CacheService;
import HooYah.Yacht.domain.Part;
import HooYah.Yacht.domain.Repair;
import HooYah.Yacht.dto.part.AddPartDto;
import HooYah.Yacht.dto.part.PartDto;
import HooYah.Yacht.dto.part.UpdatePartDto;
import HooYah.Yacht.event.CreatePartEvent;
import HooYah.Yacht.event.DeletedEvent;
import HooYah.Yacht.event.NextRepairDateChangedEvent;
import HooYah.Yacht.excetion.CustomException;
import HooYah.Yacht.excetion.ErrorCode;
import HooYah.Yacht.publisher.MessagePublisher;
import HooYah.Yacht.repository.PartRepository;
import HooYah.Yacht.repository.RepairRepository;
import HooYah.Yacht.webclient.WebClient;
import HooYah.Yacht.webclient.WebClient.HttpMethod;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
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

    private final TransactionTemplate transactionTemplate;

    private final CacheService yachtCacheService;
    private final WebClient webClient;

    private final MessagePublisher<DeletedEvent> partDeleteMessagePublisher;
    private final MessagePublisher<NextRepairDateChangedEvent> partIntervalUpdateMessagePublisher;
    private final MessagePublisher<CreatePartEvent> partCreateMessagePublisher;

    Log log = LogFactory.getLog("PartService");

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

//        if(dto.getLastRepair() != null)
//            repairService.addRepair(newPart, "Auto Generated", dto.getLastRepair(), userId);
        partCreateMessagePublisher.publish(new CreatePartEvent(newPart.getId(), newPart.getYachtId(), dto.getLastRepair(), userId)); // can null lastRepair :: 버려짐

        return newPart;
    }

    public List<Part> addPartList(Long yachtId, List<AddPartDto> dtoList, Long userId) {
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

        partRepository.saveAll(createdPartList);

        for(int i = 0; i < dtoList.size(); i++) {
            Part part = createdPartList.get(i);
            OffsetDateTime lastRepairDate = dtoList.get(i).getLastRepair();

            partCreateMessagePublisher.publish(new CreatePartEvent(part.getId(), part.getYachtId(), lastRepairDate, userId));
        }

        return createdPartList;
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
            List<PartDto.of(part, LastRepair)>

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

    public Part updatePart(UpdatePartDto dto, Long userId) {
        Part part = partRepository.findById(dto.getId()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND)
        );

        validateYachtUser(part.getYachtId(), userId);

        part.update(dto.getName(), dto.getManufacturer(), dto.getModel());
        boolean isChangedInterval = part.updateInterval(dto.getInterval());

        final Part createdPart = partRepository.save(part);

        if(isChangedInterval) {
            repairRepository.findByIdOrderByRepairDateDesc(part.getId())
                .ifPresent((lastRepair)-> {
                    // if exist last repair
                    partIntervalUpdateMessagePublisher.publish(
                        new NextRepairDateChangedEvent(
                            createdPart.getId(),
                            userId,
                            createdPart.getYachtId(),
                            createdPart.nextRepairDate(lastRepair.getRepairDate())
                        )
                    );
                });
        }

        return part;
    }

    public void deletePart(Long id, Long userId) {
        Part part = partRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND)
        );

        validateYachtUser(part.getYachtId(), userId);

        partRepository.delete(part);

        partDeleteMessagePublisher.publish(new DeletedEvent(userId, id));
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

    public void deleteByYachtId(Long yachtId, Long userId) {
        List<Long> partIdList = transactionTemplate.execute((status)-> {
            List<Part> partList = partRepository.findPartListByYacht(yachtId);
            partRepository.deleteAll(partList);
            return partList.stream().map(Part::getId).toList();
        });

        partIdList.forEach(partId -> {
            partDeleteMessagePublisher.publish(new DeletedEvent(partId, userId));
        });
    }

}
