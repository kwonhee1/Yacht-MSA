package HooYah.Yacht.controller;

import HooYah.Yacht.SuccessResponse;
import HooYah.Yacht.dto.part.AddPartDto;
import HooYah.Yacht.dto.part.UpdatePartDto;
import HooYah.Yacht.dto.part.PartDto;
import HooYah.Yacht.service.PartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/part/api")
public class PartController {

    private final PartService partService;

    @GetMapping("/{yachtId}")
    public ResponseEntity getPartListByYacht(
            @PathVariable("yachtId") Long yachtId,
            @RequestHeader("userId") Long userId
    ) {
        List<PartDto> dtoList = partService.getPartListByYacht(yachtId, userId);
        return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.OK.value(), "success", Map.of("partList", dtoList)));
    }

    @PostMapping
    public ResponseEntity addPart(
            @RequestBody @Valid AddPartDto dto,
            @RequestHeader("userId") Long userId
    ) {
        partService.addPart(dto, userId);
        return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.OK.value(), "success", null));
    }

    @PutMapping
    public ResponseEntity updatePart(
            @RequestBody @Valid UpdatePartDto dto,
            @RequestHeader("userId") Long userId
    ) {
        partService.updatePart(dto, userId);
        return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.OK.value(), "success", null));
    }

    @DeleteMapping("/{partId}")
    public ResponseEntity deletePart(
            @PathVariable("partId") Long partId,
            @RequestHeader("userId") Long userId
    ) {
        partService.deletePart(partId, userId);
        return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.OK.value(), "success", null));
    }

//    private Long getUserId(HttpServletRequest request) {
//        String userIdHeader = request.getHeader("userId");
//
//        if(userIdHeader == null || userIdHeader.isEmpty())
//            throw new CustomException(ErrorCode.UN_AUTHORIZATION);
//
//        return Long.parseLong(userIdHeader);
//    }

}
