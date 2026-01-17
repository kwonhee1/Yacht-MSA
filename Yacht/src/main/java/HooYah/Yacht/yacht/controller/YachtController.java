package HooYah.Yacht.yacht.controller;

import HooYah.Yacht.common.SuccessResponse;
import HooYah.Yacht.common.excetion.CustomException;
import HooYah.Yacht.common.excetion.ErrorCode;
import HooYah.Yacht.yacht.domain.Yacht;
import HooYah.Yacht.yacht.dto.request.CreateYachtDto;
import HooYah.Yacht.yacht.dto.request.UpdateYachtDto;
import HooYah.Yacht.yacht.service.YachtService;
import HooYah.Yacht.webclient.WebClient;
import HooYah.Yacht.webclient.WebClient.HttpMethod;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/yacht/api")
public class YachtController {

    private final YachtService yachtService;
    private final WebClient webClient;

    Logger logger = LoggerFactory.getLogger(YachtController.class);

    @Value("${web-client.gateway}")
    private String gatewayURL;

    @Value("${web-client.part-create}")
    private String partCreateURI;
    
    // todo : test!
    @PostMapping
    public ResponseEntity createYacht(@RequestBody @Valid CreateYachtDto dto, HttpServletRequest request) {
        Yacht createdYacht = yachtService.createYacht(dto, getUserId(request));

        // insert default part into createdYacht
        List<Object> partList = dto.getPartList();
        if(partList != null && !partList.isEmpty()) {
            String uri = gatewayURL + partCreateURI + "?yachtId=" + createdYacht.getId();
            try {
                webClient.webClient(uri, HttpMethod.POST, partList); // Part domain throws CustomException (JACKSON_EXCEPTION,406) --> CustomException(ErrorCode.API_FAIL)
            } catch (CustomException e) {
                // fail create Part :: just log
                logger.error("YachtDomain.YachtController.createYacht :: fail create part" + createdYacht.getId());
            }
        }

        return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.OK, "success", null));
    }

    @PutMapping
    public ResponseEntity updateYacht(@RequestBody @Valid UpdateYachtDto dto, HttpServletRequest request) {
        yachtService.updateYacht(getUserId(request), dto);
        return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.OK, "success", null));
    }

    @DeleteMapping("/{yachtId}")
    public ResponseEntity deleteYacht(HttpServletRequest request, @PathVariable("yachtId") Long yachtId) {
        yachtService.deleteYacht(getUserId(request), yachtId);
        return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.OK, "success", null));
    }

    private Long getUserId(HttpServletRequest request) {
        String userIdHeader = request.getHeader("userId");

        if(userIdHeader == null || userIdHeader.isEmpty())
            throw new CustomException(ErrorCode.UN_AUTHORIZATION);

        return Long.parseLong(userIdHeader);
    }

}
