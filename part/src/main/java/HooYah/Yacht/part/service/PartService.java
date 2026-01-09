package HooYah.Yacht.part.service;

import HooYah.Redis.RedisService;
import HooYah.Yacht.excetion.CustomException;
import HooYah.Yacht.excetion.ErrorCode;
import HooYah.Yacht.part.domain.Part;
import HooYah.Yacht.repair.domain.Repair;
import HooYah.Yacht.part.dto.request.AddPartDto;
import HooYah.Yacht.part.dto.response.PartDto;
import HooYah.Yacht.part.dto.request.UpdatePartDto;
import HooYah.Yacht.part.repository.PartRepository;
import HooYah.Yacht.repair.repository.RepairRepository;
import HooYah.Yacht.repair.service.RepairService;
import HooYah.Yacht.webclient.WebClient;
import HooYah.Yacht.webclient.WebClient.HttpMethod;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PartService {

    private final PartRepository partRepository;
    private final RepairRepository repairRepository;

    private final RedisService yachtRedisService;
    private final WebClient webClient;

    @Value("${web-client.gateway}")
    private String gatewayURL;

    @Value("${web-client.yacht}")
    private String yachtURI;

    @Value("${web-client.yacht-user}")
    private String yachtUserURI;

    public List<PartDto> getPartListByYacht(Long yachtId, Long userId) {
        validateYachtUser(yachtId, userId);

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
        validateYachtUser(yachtId, userId);

        Part newPart = Part
                .builder()
                .yachtId(yachtId)
                .name(dto.getName())
                .manufacturer(dto.getManufacturer())
                .model(dto.getModel())
                .interval(dto.getInterval())
                .build();

        return partRepository.save(newPart);
    }

    @Transactional
    public void updatePart(UpdatePartDto dto, Long userId) {
        Part part = partRepository.findById(dto.getId()).orElseThrow(
                ()-> new CustomException(ErrorCode.NOT_FOUND)
        );

        validateYachtUser(part.getYachtId(), userId);

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

        validateYachtUser(part.getYachtId(), userId);

        // delete other

        partRepository.delete(part);
    }

    private void validateYacht(Long yachtId) {
        String uri = String.format(gatewayURL + yachtURI, yachtId);

        Optional yachtUser = yachtRedisService.getOrSelect(
                yachtId,
                ()-> Optional.of(webClient.webClient(uri, HttpMethod.GET, null))
        );

        if(yachtUser.isEmpty())
            throw new CustomException(ErrorCode.CONFLICT);
    }

    private void validateYachtUser(Long yachtId, Long userId) {
        String uri = String.format(gatewayURL + yachtUserURI, yachtId, userId);

        Optional yachtUser = yachtRedisService.getOrSelect(
                yachtId, userId,
                ()-> Optional.of(webClient.webClient(uri, HttpMethod.GET, null))
        );

        if(yachtUser.isEmpty())
            throw new CustomException(ErrorCode.CONFLICT);
    }

}
