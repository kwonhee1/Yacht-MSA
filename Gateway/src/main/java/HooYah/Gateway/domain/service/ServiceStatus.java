package HooYah.Gateway.domain.service;

import java.time.LocalDateTime;

public class ServiceStatus {

    private LocalDateTime createdAt = LocalDateTime.now();

    private Long cpu;
    private Long healthyTime;

    public ServiceStatus(Long cpu, Long healthyTime) {
        this.cpu = cpu;
        this.healthyTime = healthyTime;
    }

}
