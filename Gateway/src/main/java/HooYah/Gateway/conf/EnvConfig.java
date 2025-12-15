package HooYah.Gateway.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.FileSystemResource;

@Configuration
@PropertySource(value = "file:.env.properties", ignoreResourceNotFound = true)
public class EnvConfig {
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();

        configurer.setLocation(new FileSystemResource(".env.properties"));

        // ${...} 치환자가 제대로 작동하도록 설정
        configurer.setIgnoreUnresolvablePlaceholders(false);

        return configurer;
    }
}
