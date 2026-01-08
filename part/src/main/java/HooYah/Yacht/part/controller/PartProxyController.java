package HooYah.Yacht.part.controller;

import HooYah.Yacht.SuccessResponse;
import HooYah.Yacht.excetion.CustomException;
import HooYah.Yacht.excetion.ErrorCode;
import HooYah.Yacht.part.domain.Part;
import HooYah.Yacht.part.dto.response.PartDto;
import HooYah.Yacht.part.repository.PartRepository;
import HooYah.Yacht.repair.domain.Repair;
import HooYah.Yacht.repair.repository.RepairRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class PartProxyController {

    private final PartRepository partRepository;
    private final RepairRepository repairRepository;

    @GetMapping("/part/proxy/{partId}")
    public ResponseEntity getPartById(@RequestParam("partId") Long partId) {
        Part part = partRepository.findById(partId)
                .orElseThrow(()->new CustomException(ErrorCode.NOT_FOUND));

        Repair lastRepair = repairRepository.findByIdOrderByRepairDateDesc(part.getId())
                .orElseThrow(()->new CustomException(ErrorCode.NOT_FOUND));

        return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.OK.value(), "success", PartDto.of(part, lastRepair)));
    }

}
