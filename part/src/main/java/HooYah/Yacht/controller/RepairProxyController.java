package HooYah.Yacht.controller;

import HooYah.Yacht.SuccessResponse;
import HooYah.Yacht.dto.repair.RequestRepairDto;
import HooYah.Yacht.service.RepairService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class RepairProxyController {

    private final RepairService repairService;

    @PostMapping("/repair/proxy")
    public ResponseEntity addRepair(
            @RequestBody @Valid RequestRepairDto dto,
            @RequestParam Long userId // used when Repair domain
    ) {
        repairService.addRepair(dto.getId(), dto.getContent(), dto.getDate(), userId);
        return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.OK.value(), "success", null));
    }

}
