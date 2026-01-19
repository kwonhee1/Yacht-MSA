package HooYah.Yacht.conf;

import HooYah.Yacht.webclient.TimeZone;
import HooYah.Yacht.webclient.WebClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient createClient() {
        return new WebClient(TimeZone.SEOUL, 20);
    }

}
