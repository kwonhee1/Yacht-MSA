package HooYah.Yacht.webclient;

import HooYah.Yacht.webclient.WebClient.HttpMethod;
import HooYah.Yacht.webclient.config.TimeZone;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

public class AsyncTest {

    private ObjectMapper objectMapper = new ObjectMapper();
    private WebClient webClient = new WebClient(TimeZone.SEOUL, 3);

    @Test
    public void asyncTest() {
        webClient.webClientAsync("https://www.naver.com/", HttpMethod.GET, null);
    }

}
