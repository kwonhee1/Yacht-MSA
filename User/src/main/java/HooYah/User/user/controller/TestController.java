package HooYah.User.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import HooYah.Yacht.SuccessResponse;

@Controller
public class TestController {

    @GetMapping("/test")
    public ResponseEntity<SuccessResponse> test() {
        return ResponseEntity.ok().body(new SuccessResponse(200, "success", null));
    }
}
