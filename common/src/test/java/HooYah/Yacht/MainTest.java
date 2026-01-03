package HooYah.Yacht;

import HooYah.Yacht.webclient.WebClient;
import HooYah.Yacht.webclient.WebClient.HttpMethod;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MainTest {

    @Test
    public void test() {
        Object response = new WebClient(new ObjectMapper(), 20)
                .webClient("https://httpbin.org/get", HttpMethod.GET, null);

        System.out.println(response); // body 값에 response 값이 없어서 null 발생함 (ㅋㅋㅋ 바보)

        Assertions.assertNotNull(response);
    }

}
