package HooYah.User.user.controller;

import HooYah.User.common.SuccessResponse;
import HooYah.User.user.JWTUtil;
import HooYah.User.user.domain.User;
import HooYah.User.user.dto.request.LoginDto;
import HooYah.User.user.dto.request.RegisterDto;
import HooYah.User.user.dto.response.UserInfoDto;
import HooYah.User.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
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
        Long userId = Long.parseLong(request.getHeader("userId"));
        User user = userService.findById(userId);
        return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.OK, "success", UserInfoDto.of(user)));
    }

    @DeleteMapping("/api")
    public ResponseEntity deleteUser(HttpServletRequest request) {
        Long userId = Long.parseLong(request.getHeader("userId"));
        userService.deleteUser(userId);
        return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.OK, "success", null));
    }

}
