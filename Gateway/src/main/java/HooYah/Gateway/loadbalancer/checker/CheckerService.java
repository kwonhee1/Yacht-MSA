package HooYah.Gateway.loadbalancer.checker;

import HooYah.Gateway.loadbalancer.checker.netty.NettyServer;
import HooYah.Gateway.loadbalancer.checker.status.ServiceStatus;
import HooYah.Gateway.loadbalancer.domain.service.Service;
import HooYah.Gateway.loadbalancer.checker.status.StatusMemory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckerService {

    public enum StatusType {
        Docker(1),
        TestApi(2);
        @Deprecated
        int order;
        StatusType(int order) {
            this.order = order;
        }
    }

    private Map<Service, StatusMemory> dockerStatusMemory;
    private Map<Service, StatusMemory> testApiStatusMemory;

    public CheckerService(List<Service> serviceList) {
        this.dockerStatusMemory = new HashMap(serviceList.size());
        this.testApiStatusMemory = new HashMap(serviceList.size());
        for(Service service: serviceList) {
            this.dockerStatusMemory.put(service, new StatusMemory());
            this.testApiStatusMemory.put(service, new StatusMemory());
        }

        new NettyServer(serviceList, this).start();
    }

    // service 별 status 조회
    public ServiceStatus getStatus(Service service) {
        List<ServiceStatus> dockerStatusList = dockerStatusMemory.get(service).getAllStatus();
        List<ServiceStatus> testApiStatusList = testApiStatusMemory.get(service).getAllStatus();

        for(int i = 0; i < dockerStatusList.size(); i++) {
            ServiceStatus status = getPreviousStatus(dockerStatusList.get(i), testApiStatusList.get(i));
            if(status != ServiceStatus.UNKNOWN)
                return status;
        }

        return ServiceStatus.DEAD;
    }

    private ServiceStatus getPreviousStatus(ServiceStatus dockerStatus, ServiceStatus testApiStatus) {
        if(dockerStatus != ServiceStatus.UNKNOWN)
            return dockerStatus;
        if(testApiStatus != ServiceStatus.UNKNOWN)
            return testApiStatus;
        return ServiceStatus.UNKNOWN;
    }

    public void addStatus(Service service, ServiceStatus serviceStatus, StatusType statusType) {
        if(statusType.equals(StatusType.Docker))
            dockerStatusMemory.get(service).addStatus(serviceStatus);
        else
            testApiStatusMemory.get(service).addStatus(serviceStatus);

    }

}
