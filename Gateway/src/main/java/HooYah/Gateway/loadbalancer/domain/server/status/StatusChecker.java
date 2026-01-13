package HooYah.Gateway.loadbalancer.domain.server.status;

import HooYah.Gateway.loadbalancer.domain.service.ServiceStatus;
import HooYah.Gateway.loadbalancer.domain.vo.Api;

public class StatusChecker {

    private final Api healthyApi;
    private ServiceStatus lastStatus;

    public StatusChecker(Api healthyApi) {
        this.healthyApi = healthyApi;
    }

    public void checkStatus() {

    }

    public ServiceStatus getLastStatus() {
        return lastStatus;
    }

}
