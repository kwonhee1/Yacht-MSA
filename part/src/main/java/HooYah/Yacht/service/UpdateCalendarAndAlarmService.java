package HooYah.Yacht.service;

import HooYah.Yacht.domain.Part;
import HooYah.Yacht.domain.Repair;
import HooYah.Yacht.dto.AutoGenerateRequest;
import HooYah.Yacht.repository.RepairRepository;
import HooYah.Yacht.webclient.WebClient;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateCalendarAndAlarmService {

    private final RepairRepository repairRepository;
    private final WebClient webClient;

    @Value("${web-client.gateway}")
    private String gatewayURL;
    @Value("${web-client.calendar-alarm-auto-generate}")
    private String calendarAlarmAutoGenerateURI;


    public void updateCalendarAndAlarm(Part part) {
        Optional<Repair> lastRepairOpt = repairRepository.findByIdOrderByRepairDateDesc(part.getId());

        if (lastRepairOpt.isEmpty()) {
            return;
        }

        List<AutoGenerateRequest> body = List.of(
                AutoGenerateRequest.builder()
                        .partId(part.getId())
                        .yachtId(part.getYachtId())
                        .nextRepairDate(lastRepairOpt.get().getRepairDate())
                        .build()
        );

        webClient.webClient(
                gatewayURL + calendarAlarmAutoGenerateURI,
                WebClient.HttpMethod.POST,
                body
        ).toMap();
    }

    public void updateCalendarAndAlarmList(List<Part> partList) {
        List<Long> partIdList = partList.stream().map(Part::getId).toList();
        List<Repair> lastRepairList = repairRepository.findAllLastRepair(partIdList);

        Map<Long, Repair> lastRepairMap = lastRepairList.stream()
                .collect(Collectors.toMap(
                        repair -> repair.getPart().getId(),
                        repair -> repair
                ));

        List<AutoGenerateRequest> bodyList = partList.stream()
                .map(part -> {
                    Repair lastRepair = lastRepairMap.get(part.getId());
                    if (lastRepair == null) {
                        return null;
                    }
                    return AutoGenerateRequest.builder()
                            .partId(part.getId())
                            .yachtId(part.getYachtId())
                            .nextRepairDate(lastRepair.getRepairDate())
                            .build();
                })
                .filter(java.util.Objects::nonNull)
                .toList();

        if (bodyList.isEmpty()) {
            return;
        }

        webClient.webClient(
                gatewayURL + calendarAlarmAutoGenerateURI,
                WebClient.HttpMethod.POST,
                bodyList
        );
    }
}
