package HooYah.Yacht.conf;

import java.net.http.HttpClient;
import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebClientConfig {

    @Bean
    public HttpClient createClient() {
        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(20))
                .version(HttpClient.Version.HTTP_1_1)
                .build();
    }

}
