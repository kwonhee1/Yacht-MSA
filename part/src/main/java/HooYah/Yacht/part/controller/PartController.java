package HooYah.Yacht.part.controller;

import HooYah.Yacht.SuccessResponse;
import HooYah.Yacht.excetion.CustomException;
import HooYah.Yacht.excetion.ErrorCode;
import HooYah.Yacht.part.dto.request.AddPartDto;
import HooYah.Yacht.part.dto.response.PartDto;
import HooYah.Yacht.part.dto.request.UpdatePartDto;
import HooYah.Yacht.part.service.PartService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
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
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/part")
public class PartController {

    private final PartService partService;

    @GetMapping("/{yachtId}")
    public ResponseEntity getPartListByYacht(
            @PathVariable("yachtId") Long yachtId,
            HttpServletRequest request
    ) {
        Long userId = getUserId(request);
        List<PartDto> dtoList = partService.getPartListByYacht(yachtId, userId);
        return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.OK.value(), "success", Map.of("partList", dtoList)));
    }

    @PostMapping
    public ResponseEntity addPart(
            @RequestBody @Valid AddPartDto dto,
            HttpServletRequest request
    ) {
        Long userId = getUserId(request);
        partService.addPart(dto.getYachtId(), dto, userId);
        return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.OK.value(), "success", null));
    }

    @PutMapping
    public ResponseEntity updatePart(
            @RequestBody @Valid UpdatePartDto dto,
            HttpServletRequest request
    ) {
        Long userId = getUserId(request);
        partService.updatePart(dto, userId);
        return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.OK.value(), "success", null));
    }

    @DeleteMapping("/{partId}")
    public ResponseEntity deletePart(
            @PathVariable("partId") Long partId,
            HttpServletRequest request
    ) {
        Long userId = getUserId(request);
        partService.deletePart(partId, userId);
        return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.OK.value(), "success", null));
    }

    private Long getUserId(HttpServletRequest request) {
        String userIdHeader = request.getHeader("userId");

        if(userIdHeader == null || userIdHeader.isEmpty())
            throw new CustomException(ErrorCode.UN_AUTHORIZATION);

        return Long.parseLong(userIdHeader);
    }

}
