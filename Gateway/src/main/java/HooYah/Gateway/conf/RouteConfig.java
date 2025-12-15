package HooYah.Gateway.conf;

import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {

    @Bean
    public RouteLocatorBuilder routeLocatorBuilder(
            ConfigurableApplicationContext context
    ) {
        return new RouteLocatorBuilder(context);
    }

}
