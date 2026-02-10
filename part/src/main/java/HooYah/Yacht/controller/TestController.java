package HooYah.Yacht.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.ResponseBody;
import HooYah.Yacht.SuccessResponse;

@Controller
public class TestController {

    @GetMapping("/test")
    @ResponseBody
    public ResponseEntity<SuccessResponse> test() {
        return ResponseEntity.ok().body(new SuccessResponse(200, "success", null));
    }
}
