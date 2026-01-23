package HooYah.Yacht;

import HooYah.Yacht.webclient.TimeZone;
import HooYah.Yacht.webclient.WebClient;
import HooYah.Yacht.webclient.WebClient.HttpMethod;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class YachtApplicationTests {

	@Test
	void webClientTest() {
        WebClient webClient = new WebClient(TimeZone.SEOUL, 3);
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                webClient.webClient("localhost", HttpMethod.GET, null).toMap()
        ); // throws By Illegal URL
	}

}
