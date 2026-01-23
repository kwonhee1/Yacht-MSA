package HooYah.User.user.controller;

import HooYah.User.user.JWTUtil;
import HooYah.User.user.domain.User;
import HooYah.User.user.dto.request.LoginDto;
import HooYah.User.user.dto.request.RegisterDto;
import HooYah.User.user.dto.response.UserInfoDto;
import HooYah.User.user.repository.UserRepository;
import HooYah.User.user.service.UserService;
import HooYah.Yacht.SuccessResponse;
import HooYah.Yacht.excetion.CustomException;
import HooYah.Yacht.excetion.ErrorCode;
import HooYah.Yacht.util.ListUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    @PostMapping("/public/register")
    public ResponseEntity register(@RequestBody @Valid RegisterDto dto) {
        userService.registerWithEmail(dto);
        return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.OK.value(), "success", null));
    }

    @PostMapping("/public/login")
    public ResponseEntity login(@RequestBody @Valid LoginDto dto) {
        User user = userService.login(dto);
        String token = JWTUtil.generateToken(user.getId());

        return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.OK.value(), "success", Map.of("token", token)));
    }

    @GetMapping("/public/email-check")
    public ResponseEntity emailCheck(@RequestParam("email") String email) {
        boolean isExist = userService.findByEmail(email).isPresent();

        if(isExist)
            return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.OK.value(), "exist", null));
        else
            return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.OK.value(), "not exist", null));
    }

    @GetMapping("/api")
    public ResponseEntity getUser(HttpServletRequest request) {
        User user = userService.findById(getUserId(request));
        return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.OK.value(), "success", UserInfoDto.of(user)));
    }

    @DeleteMapping("/api")
    public ResponseEntity deleteUser(HttpServletRequest request) {
        userService.deleteUser(getUserId(request));
        return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.OK.value(), "success", null));
    }

    // proxy
    @PostMapping("/proxy/user-list")
    public ResponseEntity getUserList(@RequestBody List<Long> userIdList) {
        List<User> selectedUserList = userService.getUserList(userIdList);
        List<User> sortedUserList = ListUtil.sortByRequestOrder(userIdList, selectedUserList, (user)->user.getId());

        List<UserInfoDto> userInfoList = sortedUserList.stream().map(UserInfoDto::of).toList();
        return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.OK.value(), "success", userInfoList));
    }

    @PostMapping("/proxy/user-token")
    public ResponseEntity getUserToken(@RequestBody List<Long> userIdList) {
        List<User> selectedUserList = userRepository.findAllById(userIdList);
        List<User> sortedUserList = ListUtil.sortByRequestOrder(userIdList, selectedUserList, (user)->user.getId());

        return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.OK.value(), "success", sortedUserList));
    }

    private Long getUserId(HttpServletRequest request) {
        String userIdHeader = request.getHeader("userId");

        if(userIdHeader == null || userIdHeader.isEmpty())
            throw new CustomException(ErrorCode.UN_AUTHORIZATION);

        return Long.parseLong(userIdHeader);
    }

}
