package HooYah.User.user.controller;

import HooYah.User.common.SuccessResponse;
import HooYah.User.common.excetion.CustomException;
import HooYah.User.common.excetion.ErrorCode;
import HooYah.User.user.JWTUtil;
import HooYah.User.user.domain.User;
import HooYah.User.user.dto.request.LoginDto;
import HooYah.User.user.dto.request.RegisterDto;
import HooYah.User.user.dto.response.UserInfoDto;
import HooYah.User.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/public/register")
    public ResponseEntity register(@RequestBody @Valid RegisterDto dto) {
        userService.registerWithEmail(dto);
        return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.OK, "success", null));
    }

    @PostMapping("/public/login")
    public ResponseEntity login(@RequestBody @Valid LoginDto dto) {
        User user = userService.login(dto);
        String token = JWTUtil.generateToken(user.getId());

        return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.OK, "success", Map.of("token", token)));
    }

    @GetMapping("/public/email-check")
    public ResponseEntity emailCheck(@RequestParam("email") String email) {
        boolean isExist = userService.findByEmail(email).isPresent();

        if(isExist)
            return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.OK, "exist", null));
        else
            return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.OK, "not exist", null));
    }

    @GetMapping("/api")
    public ResponseEntity getUser(HttpServletRequest request) {
        User user = userService.findById(getUserId(request));
        return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.OK, "success", UserInfoDto.of(user)));
    }

    @DeleteMapping("/api")
    public ResponseEntity deleteUser(HttpServletRequest request) {
        userService.deleteUser(getUserId(request));
        return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.OK, "success", null));
    }

    /*
      * throw
      *      CONFLICT : input (UserIdList.size) != output (UserInfoList.size) :: 잘못된 userId값이 포함되었을 경우 (중복 값인 경우도 포함됨)
     */
    @PostMapping("/proxy/user-list")
    public ResponseEntity getUserList(@RequestBody List<Long> userIdList) {
        List<User> userList = userService.getUserList(userIdList);
        List<UserInfoDto> userInfoList = userList.stream().map(UserInfoDto::of).toList();
        return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.OK, "success", userInfoList));
    }

    private Long getUserId(HttpServletRequest request) {
        String userIdHeader = request.getHeader("userId");

        if(userIdHeader == null || userIdHeader.isEmpty())
            throw new CustomException(ErrorCode.UN_AUTHORIZATION);

        return Long.parseLong(userIdHeader);
    }

}
