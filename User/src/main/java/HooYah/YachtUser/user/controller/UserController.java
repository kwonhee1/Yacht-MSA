package HooYah.YachtUser.user.controller;

import HooYah.YachtUser.common.SuccessResponse;
import HooYah.YachtUser.user.JWTUtil;
import HooYah.YachtUser.user.domain.User;
import HooYah.YachtUser.user.dto.request.LoginDto;
import HooYah.YachtUser.user.dto.request.RegisterDto;
import HooYah.YachtUser.user.dto.response.UserInfoDto;
import HooYah.YachtUser.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @PostMapping("/public/user/register")
    public ResponseEntity register(@RequestBody @Valid RegisterDto dto) {
        userService.registerWithEmail(dto);
        return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.OK, "success", null));
    }

    @PostMapping("/public/user/login")
    public ResponseEntity login(@RequestBody @Valid LoginDto dto) {
        User user = userService.login(dto);
        String token = JWTUtil.generateToken(user.getId());

        return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.OK, "success", Map.of("token", token)));
    }

    @GetMapping("/public/user/email-check")
    public ResponseEntity emailCheck(@RequestParam("email") String email) {
        boolean isExist = userService.findByEmail(email).isPresent();

        if(isExist)
            return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.OK, "exist", null));
        else
            return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.OK, "not exist", null));
    }

    @GetMapping("/api/user")
    public ResponseEntity getUser(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.OK, "success", UserInfoDto.of(user)));
    }

    @DeleteMapping("/api/user")
    public ResponseEntity deleteUser(@AuthenticationPrincipal User user) {
        userService.deleteUser(user);
        return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.OK, "success", null));
    }

}
