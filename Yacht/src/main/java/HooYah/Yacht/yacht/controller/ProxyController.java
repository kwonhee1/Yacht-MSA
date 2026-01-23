package HooYah.Yacht.yacht.controller;

import HooYah.Yacht.SuccessResponse;
import HooYah.Yacht.excetion.CustomException;
import HooYah.Yacht.excetion.ErrorCode;
import HooYah.Yacht.util.ListUtil;
import HooYah.Yacht.yacht.domain.Yacht;
import HooYah.Yacht.yacht.repository.YachtRepository;
import HooYah.Yacht.yacht.repository.YachtUserRepository;
import HooYah.Yacht.yacht.service.YachtUserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/yacht/proxy")
@RequiredArgsConstructor
public class ProxyController {

    private final YachtRepository yachtRepository;
    private final YachtUserRepository yachtUserRepository;

    private final YachtUserService yachtUserService;

    @GetMapping("/validate-yacht")
    public ResponseEntity validateYacht(@RequestParam("yachtId") Long yachtId) {
        Yacht yacht = yachtRepository.findById(yachtId).orElseThrow(
                ()->new CustomException(ErrorCode.NOT_FOUND)
        );
        return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.OK.value(), "success", yacht));
    }

    @GetMapping("/validate-user")
    public ResponseEntity validateUser(
            @RequestParam("userId") Long userId ,
            @RequestParam("yachtId") Long yachtId
    ) {
        Yacht yacht = yachtUserRepository.findYacht(yachtId, userId).orElseThrow(
                ()->new CustomException(ErrorCode.NOT_FOUND)
        );
        return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.OK.value(), "success", yacht));
    }

    @PostMapping
    public ResponseEntity getYachtList(@RequestBody List<Long> yachtIdList) {
        List<Yacht> yachtList = yachtRepository.findAllById(yachtIdList);
        List<Yacht> sortedYachtList = ListUtil.sortByRequestOrder(yachtIdList, yachtList, Yacht::getId);
        return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.OK.value(), "success", sortedYachtList));
    }

    @PostMapping("/yacht-user")
    public ResponseEntity getYachtUserIdList(@RequestBody List<Long> userIdList) {
        List<List<Long>> selectedList = yachtUserService.getYachtUserIdList(userIdList);
        // already sorted!
        return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.OK.value(), "success", selectedList));
    }

    // userId -> List<Long::YachtId>
    @GetMapping("/get-yacht-list")
    public ResponseEntity getYachtIdListByUser(@RequestParam("userId") Long userId) {
        List<Yacht> yachtList = yachtUserRepository.findAllYachtByUserId(userId);
        return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.OK.value(), "success", yachtList.stream().map(Yacht::getId).toList()));
    }

}