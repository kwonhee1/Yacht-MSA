package HooYah.Yacht.part.controller;

import HooYah.Yacht.part.service.CreatePartService;
import HooYah.Yacht.util.ListUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import HooYah.Yacht.SuccessResponse;
import HooYah.Yacht.excetion.CustomException;
import HooYah.Yacht.excetion.ErrorCode;
import HooYah.Yacht.part.domain.Part;
import HooYah.Yacht.part.dto.request.AddPartDto;
import HooYah.Yacht.part.dto.response.PartDto;
import HooYah.Yacht.part.repository.PartRepository;
import HooYah.Yacht.part.service.PartService;
import HooYah.Yacht.repair.domain.Repair;
import HooYah.Yacht.repair.repository.RepairRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/part/proxy")
public class PartProxyController {

    private final PartRepository partRepository;
    private final RepairRepository repairRepository;
    private final PartService partService;
    private final ObjectMapper objectMapper;
    private final CreatePartService createPartService;

    @GetMapping("/{partId}")
    public ResponseEntity getPartById(@PathVariable("partId") Long partId) {
        Part part = partRepository.findById(partId)
                .orElseThrow(()->new CustomException(ErrorCode.NOT_FOUND));

        Repair lastRepair = repairRepository.findByIdOrderByRepairDateDesc(part.getId())
                .orElseThrow(()->new CustomException(ErrorCode.NOT_FOUND));

        return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.OK.value(), "success", PartDto.of(part, lastRepair)));
    }

    //  proxy getPartInfoList (post: /part/proxy, body: List<Long>) : return List<PartDto>
    @PostMapping
    public ResponseEntity getPartInfoList(@RequestBody List<Long> partIdList) {
        List<PartDto> partDtoList = partService.getPartListByIdList(partIdList);
        List<PartDto> sortedPartDtoList = ListUtil.sortByRequestOrder(partIdList, partDtoList,
            PartDto::getId);
        return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.OK.value(), "success", sortedPartDtoList));
    }

    //  proxy getPartNameList (post: /part/proxy/name, body: List<Long::partId>) : return List<String::partName>
    @PostMapping("/name")
    public ResponseEntity getPartName(@RequestBody List<Long> partIdList) {
        List<Part> partList = partRepository.findAllById(partIdList);
        List<Part> sortedPartList = ListUtil.sortByRequestOrder(partIdList, partList, Part::getId);
        List<String> partNameList = sortedPartList
            .stream()
            .map(Part::getName)
            .toList();

        return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.OK.value(), "success", partNameList));
    }

    @PostMapping("/create")
    public ResponseEntity addPartList(
        @RequestParam Long yachtId,
        @RequestBody(required = false) List<AddPartDto> dtoList // if cast fail -> throws JacksonException (maybe...?)
    ) {
        if(dtoList == null || dtoList.isEmpty()) {
            return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.CONFLICT.value(), "fail", null));
        }

        createPartService.addPartList(yachtId, dtoList);

        return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.OK.value(), "success", null));
    }
}
