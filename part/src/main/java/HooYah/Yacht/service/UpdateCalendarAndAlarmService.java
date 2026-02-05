package HooYah.Yacht.service;

import HooYah.Yacht.domain.Part;
import HooYah.Yacht.domain.Repair;
import HooYah.Yacht.repository.RepairRepository;
import HooYah.Yacht.webclient.WebClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

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

        Map<String, Object> body = Map.of(
                "partId", part.getId(),
                "yachtId", part.getYachtId(),
                "nextRepairDate", lastRepairOpt.get().getRepairDate()
        );

        webClient.webClient(
                gatewayURL + calendarAlarmAutoGenerateURI,
                WebClient.HttpMethod.POST,
                body
        ).toMap();
    }
}
