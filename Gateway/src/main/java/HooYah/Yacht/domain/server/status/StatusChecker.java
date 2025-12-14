package HooYah.Yacht.domain.server.status;

import HooYah.Yacht.domain.service.ServiceStatus;
import com.google.protobuf.Api;

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
