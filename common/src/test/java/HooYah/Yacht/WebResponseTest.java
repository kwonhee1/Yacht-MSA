package HooYah.Yacht;

import HooYah.Yacht.webclient.config.TimeZone;
import HooYah.Yacht.webclient.WebClient;
import HooYah.Yacht.webclient.response.WebResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class WebResponseTest {

    private ObjectMapper objectMapper = new ObjectMapper();
    private WebClient webClient = new WebClient(TimeZone.SEOUL, 3);

    @Test
    public void MapTest() throws JsonProcessingException {
        String responseStr = objectMapper.writeValueAsString(new SuccessResponse(200, "success", new Data(1L, "name")));

        Map<String, Object> webResponse = new WebResponse(responseStr).toMap();

        Assertions.assertEquals(1, webResponse.get("id"));
        Assertions.assertEquals("name", webResponse.get("name"));
    }

    @Test
    public void LongListTest() throws JsonProcessingException {
        String responseStr = objectMapper.writeValueAsString(new SuccessResponse(200, "success", List.of(1,2,3,4,5)));

        List<Long> webResponse = new WebResponse(responseStr).toLongList();

        Assertions.assertTrue(webResponse.containsAll(List.of(1L, 2L, 3L, 4L, 5L)));
    }

    @Test
    public void ListTest() throws JsonProcessingException {
        String responseStr = objectMapper.writeValueAsString(new SuccessResponse(200, "success", List.of(new Data(1L, "name"), new Data(2L, "name2"))));

        List webResponse = new WebResponse(responseStr).toList();

        Assertions.assertEquals(2, webResponse.size());
    }

    record Data (
        Long id,
        String name
    ) { }

}
