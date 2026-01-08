package HooYah.Yacht.controller;

import HooYah.Yacht.SuccessResponse;
import HooYah.Yacht.excetion.CustomException;
import HooYah.Yacht.excetion.ErrorCode;
import HooYah.Yacht.service.AlarmService;
import HooYah.Yacht.service.FCMService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/alarm")
public class AlarmController {

    private final AlarmService alarmService;
    private final FCMService fCMService;

    @GetMapping
    public ResponseEntity getAlarmList(HttpServletRequest request) {
        Long userId = getUserId(request);
        return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.OK.value(), "success", alarmService.getAlarmList(userId)));
    }

    @PostMapping
    @Scheduled(cron = "0 0 9 * * *") // 매일 아침 9시 전송됨
    public ResponseEntity sendAlarm() {
        log.info("total send alarm start");
        alarmService.sendAlarm();
        return ResponseEntity.ok().build();
    }

    private Long getUserId(HttpServletRequest request) {
        String userIdHeader = request.getHeader("userId");

        if(userIdHeader == null || userIdHeader.isEmpty())
            throw new CustomException(ErrorCode.UN_AUTHORIZATION);

        return Long.parseLong(userIdHeader);
    }

}
