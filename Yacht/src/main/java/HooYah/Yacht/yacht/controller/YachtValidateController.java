package HooYah.Yacht.yacht.controller;

import HooYah.Yacht.common.SuccessResponse;
import HooYah.Yacht.common.excetion.CustomException;
import HooYah.Yacht.common.excetion.ErrorCode;
import HooYah.Yacht.yacht.domain.Yacht;
import HooYah.Yacht.yacht.repository.YachtRepository;
import HooYah.Yacht.yacht.repository.YachtUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/proxy")
@RequiredArgsConstructor
public class YachtValidateController {

    private final YachtRepository yachtRepository;
    private final YachtUserRepository yachtUserRepository;

    @GetMapping("/validate-yacht")
    public ResponseEntity validateYacht(@RequestParam("yachtId") Long yachtId) {
        Yacht yacht = yachtRepository.findById(yachtId).orElseThrow(
                ()->new CustomException(ErrorCode.NOT_FOUND)
        );
        return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.OK, "success", yacht));
    }

    @GetMapping("/validate-user")
    public ResponseEntity validateUser(
            @RequestParam("userId") Long userId ,
            @RequestParam("yachtId") Long yachtId
    ) {
        Yacht yacht = yachtUserRepository.findYacht(yachtId, userId).orElseThrow(
                ()->new CustomException(ErrorCode.NOT_FOUND)
        );
        return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.OK, "success", yacht));
    }

}