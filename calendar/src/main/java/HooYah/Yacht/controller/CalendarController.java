package HooYah.Yacht.controller;

import HooYah.Yacht.dto.request.CalendarCreateRequest;
import HooYah.Yacht.dto.request.CalendarUpdateRequest;
import HooYah.Yacht.dto.response.CalendarInfo;
import HooYah.Yacht.excetion.CustomException;
import HooYah.Yacht.excetion.ErrorCode;
import HooYah.Yacht.service.CalendarService;
import HooYah.Yacht.SuccessResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/calendar/api")
public class CalendarController {

    private final CalendarService calendarService;

    @PostMapping
    public ResponseEntity<SuccessResponse> createCalendar(
            @Valid @RequestBody CalendarCreateRequest dto,
            HttpServletRequest request
    ) {
        Long userId = getUserId(request);

        CalendarInfo response = calendarService.createCalendarByUser(dto, userId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SuccessResponse(HttpStatus.CREATED.value(), "success", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse> getCalendar(
            @PathVariable Long id,
            HttpServletRequest request) {
        Long userId = getUserId(request);
        CalendarInfo response = calendarService.getCalendar(id, userId);
        return ResponseEntity.ok(new SuccessResponse(HttpStatus.OK.value(), "success", response));
    }

    @GetMapping
    public ResponseEntity<SuccessResponse> getCalendars(
            @RequestParam(value = "partId", required = false) Long partId, // api 분리 필요 (아니 partId값이 있는 경우는 또 뭐임?)
            HttpServletRequest request
    ) {
        Long userId = getUserId(request);
        List<CalendarInfo> responses = calendarService.getCalendars(userId);
        return ResponseEntity.ok(new SuccessResponse(HttpStatus.OK.value(), "success", responses));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SuccessResponse> updateCalendar(
            @PathVariable Long id,
            @Valid @RequestBody CalendarUpdateRequest request,
            HttpServletRequest httpRequest
    ) {
        Long userId = getUserId(httpRequest);
        calendarService.updateCalendar(id, request, userId);
        return ResponseEntity.ok(new SuccessResponse(HttpStatus.OK.value(), "success", null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponse> deleteCalendar(
            @PathVariable Long id,
            HttpServletRequest request
    ) {
        Long userId = getUserId(request);
        calendarService.deleteCalendar(id, userId);
        return ResponseEntity.ok(new SuccessResponse(HttpStatus.OK.value(), "success", null));
    }

    private Long getUserId(HttpServletRequest request) {
        String userIdHeader = request.getHeader("userId");

        if(userIdHeader == null || userIdHeader.isEmpty())
            throw new CustomException(ErrorCode.UN_AUTHORIZATION);

        return Long.parseLong(userIdHeader);
    }

}

