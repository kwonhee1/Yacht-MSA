package HooYah.Gateway.loadbalancer.checker;

import HooYah.Gateway.loadbalancer.checker.netty.NettyServer;
import HooYah.Gateway.loadbalancer.checker.status.ServiceStatus;
import HooYah.Gateway.loadbalancer.domain.pod.Pod;
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

    private Map<Pod, StatusMemory> dockerStatusMemory;
    private Map<Pod, StatusMemory> testApiStatusMemory;

    public CheckerService(List<Pod> podList) {
        this.dockerStatusMemory = new HashMap(podList.size());
        this.testApiStatusMemory = new HashMap(podList.size());
        for(Pod pod: podList) {
            this.dockerStatusMemory.put(pod, new StatusMemory());
            this.testApiStatusMemory.put(pod, new StatusMemory());
        }

        new NettyServer(podList, this).start();
    }

    // service 별 status 조회
    public ServiceStatus getStatus(Pod pod) {
        List<ServiceStatus> dockerStatusList = dockerStatusMemory.get(pod).getAllStatus();
        List<ServiceStatus> testApiStatusList = testApiStatusMemory.get(pod).getAllStatus();

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

        public void addStatus(Pod pod, ServiceStatus serviceStatus, StatusType statusType) {

            if(statusType.equals(StatusType.Docker))

                dockerStatusMemory.get(pod).addStatus(serviceStatus);

            else

                testApiStatusMemory.get(pod).addStatus(serviceStatus);

        }

}
