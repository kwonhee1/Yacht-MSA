package HooYah.Yacht;

import HooYah.Yacht.webclient.TimeZone;
import HooYah.Yacht.webclient.WebClient;
import HooYah.Yacht.webclient.WebClient.HttpMethod;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class MainTest {

    @Test
    public void test() {
        Object response = new WebClient(TimeZone.SEOUL, 20)
                .webClient("https://httpbin.org/get", HttpMethod.GET, null);

        System.out.println(response); // body 값에 response 값이 없어서 null 발생함 (ㅋㅋㅋ 바보)

        Assertions.assertNotNull(response);
    }

}
