package HooYah.Yacht.conf;

import HooYah.Yacht.webclient.WebClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient createClient() {
        return new WebClient(new ObjectMapper(), 20);
    }

}
