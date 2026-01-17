package HooYah.Yacht.yacht.controller;

import HooYah.Redis.CacheService;
import HooYah.Yacht.common.SuccessResponse;
import HooYah.Yacht.common.excetion.CustomException;
import HooYah.Yacht.common.excetion.ErrorCode;
import HooYah.Yacht.yacht.dto.request.InviteYachtDto;
import HooYah.Yacht.yacht.dto.response.ResponseYachtDto;
import HooYah.Yacht.yacht.service.YachtUserService;
import HooYah.Yacht.webclient.WebClient;
import HooYah.Yacht.webclient.WebClient.HttpMethod;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/yacht/api")
public class YachtUserController {

    private final YachtUserService yachtUserService;
    private final WebClient webClient;
    private final CacheService userCacheService;

    @Value("${web-client.gateway}")
    private String gatewayURL;

    @Value("${web-client.user-list}")
    private String userListURI;

    @GetMapping
    public ResponseEntity getYachtList(HttpServletRequest request) {
        List<ResponseYachtDto> yachtList = yachtUserService.yachtList(getUserId(request));
        return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.OK, "success", Map.of("list", yachtList)));
    }

    @GetMapping("/user/{yachtId}")
    public ResponseEntity getYachtUserList(
            HttpServletRequest request ,
            @PathVariable("yachtId") Long yachtId
    ) {
        List<Long> yachtUserIdList = yachtUserService.yachtUserIdList(yachtId, getUserId(request)); // yacht user 들의 id list

        List<?> userList = userCacheService.getListOrSelect(
                yachtUserIdList,
                () -> (List) webClient.webClient(gatewayURL + userListURI, HttpMethod.POST, yachtUserIdList)
        );

        return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.OK, "success", Map.of("userList", userList)));
    }

    @GetMapping("/invite")
    public ResponseEntity getYachtInviteCode(@RequestParam("yachtId") Long yachtId, HttpServletRequest request) {
        Long code = yachtUserService.getYachtCode(yachtId, getUserId(request));
        return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.OK, "success", Map.of("code", code)));
    }

    @PostMapping("/invite")
    public ResponseEntity inviteWithCode(@RequestBody @Valid InviteYachtDto dto, HttpServletRequest request) {
        yachtUserService.inviteYachtByHash(dto.getCode(), getUserId(request));
        return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.OK, "success", null));
    }

    private Long getUserId(HttpServletRequest request) {
        String userIdHeader = request.getHeader("userId");

        if(userIdHeader == null || userIdHeader.isEmpty())
            throw new CustomException(ErrorCode.UN_AUTHORIZATION);

        return Long.parseLong(userIdHeader);
    }

}
