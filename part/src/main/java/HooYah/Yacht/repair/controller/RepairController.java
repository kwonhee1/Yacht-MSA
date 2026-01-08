package HooYah.Yacht.repair.controller;

import HooYah.Redis.RedisService;
import HooYah.Yacht.repair.domain.Repair;
import HooYah.Yacht.repair.dto.RequestRepairDto;
import HooYah.Yacht.repair.dto.RepairDto;
import HooYah.Yacht.SuccessResponse;
import HooYah.Yacht.excetion.CustomException;
import HooYah.Yacht.excetion.ErrorCode;
import HooYah.Yacht.repair.service.RepairService;
import HooYah.Yacht.webclient.WebClient;
import HooYah.Yacht.webclient.WebClient.HttpMethod;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/repair")
public class RepairController {

    private final RepairService repairService;
    private final RedisService userRedisService;

    private final WebClient webClient;

    @Value("${web-client.gateway}")
    private String gatewayURL;
    @Value("${web-client.user-list}")
    private String userListURI;

    @GetMapping("{partId}")
    public ResponseEntity getPairList(
            @PathVariable("partId") Long partId,
            HttpServletRequest request
    ){
        Long userId = getUserId(request);
        List<Repair> repairList = repairService.getRepairListByPart(partId, userId);

        List<Long> repairUserIdList = repairList.stream().map(Repair::getUserId).toList();
        List<?> repairUserInfoList = userRedisService.getListOrSelect(
                repairUserIdList,
                ()-> (List) webClient.webClient(gatewayURL + userListURI, HttpMethod.POST, repairUserIdList) // 순서 보장함
        );

        List<RepairDto> response = new ArrayList<>();

        for(int i = 0; i < repairList.size(); i++){
            response.add(RepairDto.of(repairList.get(i), repairUserInfoList.get(i)));
        }

        return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.OK.value(), "success", Map.of("repairList", response)));
    }

    @PostMapping
    public ResponseEntity addRepair(
            @RequestBody @Valid RequestRepairDto dto,
            HttpServletRequest request
    ) {
        Long userId = getUserId(request);
        repairService.addRepair(dto.getId(), dto.getContent(), dto.getDate(), userId);
        return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.OK.value(), "success", null));
    }

    @PutMapping
    public ResponseEntity updateRepair(
            @RequestBody @Valid RequestRepairDto dto,
            HttpServletRequest request
    ) {
        Long userId = getUserId(request);
        repairService.updateRepair(dto.getId(), dto.getContent(), dto.getDate(), userId);
        return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.OK.value(), "success", null));
    }

    @DeleteMapping("/{repairId}")
    public ResponseEntity deleteRepair(
            @PathVariable("repairId") Long repairId,
            HttpServletRequest request
    ) {
        Long userId = getUserId(request);
        repairService.deleteRepair(repairId, userId);
        return  ResponseEntity.ok().body(new SuccessResponse(HttpStatus.OK.value(), "success", null));
    }

    private Long getUserId(HttpServletRequest request) {
        String userIdHeader = request.getHeader("userId");

        if(userIdHeader == null || userIdHeader.isEmpty())
            throw new CustomException(ErrorCode.UN_AUTHORIZATION);

        return Long.parseLong(userIdHeader);
    }

}
