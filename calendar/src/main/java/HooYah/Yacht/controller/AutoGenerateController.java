package HooYah.Yacht.controller;

import HooYah.Yacht.SuccessResponse;
import HooYah.Yacht.dto.request.AutoGenerateRequest;
import HooYah.Yacht.service.CalendarAlarmAutoGeneratorService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@RequestMapping("/calendar/proxy")
public class AutoGenerateController {

    private final CalendarAlarmAutoGeneratorService calendarAlarmAutoGeneratorService;

    @PostMapping
    public ResponseEntity<SuccessResponse> generateCalendarAndAlarm(
            @Valid @RequestBody List<AutoGenerateRequest> request
    ) {
        calendarAlarmAutoGeneratorService.generateList(request);

        return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.CREATED.value(), "success", null));
    }

}

