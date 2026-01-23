package HooYah.Yacht.webclient;

import HooYah.Yacht.SuccessResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import java.util.Map;

public class WebResponse {

    private final Object response;

    public WebResponse(String response) {
        this.response = toSuccessResponse(response);
    }

    private Object toSuccessResponse(String response) {
        SuccessResponse successResponse = Mapper.toObject(response, SuccessResponse.class);
        return successResponse.getResponse();
    }

    public Map toMap() {
        return (Map<String, Object>)response;
    }

    public List toList() {
        return Mapper.convert(response, List.class);
    }

    public List<Long> toLongList() {
        return Mapper.convert(response, new TypeReference<List<Long>>() { });
    }

}
