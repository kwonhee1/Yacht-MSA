package HooYah.Gateway.provider;

import HooYah.Gateway.loadbalancer.domain.pod.Pod;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Limiter {

    private final static long DEFAULT_LIMITER_MAX_COUNT = 6;

    private final Map<Pod, Long> concurrentCount = new HashMap<>();
    private final Map<Pod, Long> maxCount;

    public Limiter(Map<Pod, Long> maxCount) {
        this.maxCount = maxCount;

        for(Pod pod : maxCount.keySet())
            concurrentCount.put(pod, 0L);
    }

    public Limiter(List<Pod> pods) {
        this(pods.stream().collect(
                Collectors.toMap( // collect to Map<Pod, Long>
                        (p)->p,  // pod -> pod
                        (p)->DEFAULT_LIMITER_MAX_COUNT // ? -> DefaultLimitMaxCount
                )
        ));
    }

    public boolean tryUpCount(Pod pod) {
        synchronized (pod) {
            Long current = concurrentCount.get(pod);
            Long max = maxCount.get(pod);

            if (current < max) {
                concurrentCount.put(pod, current + 1);
                return true;
            }
            return false;
        }
    }

    public void downCount(Pod pod) {
        synchronized (pod) {
            Long current = concurrentCount.get(pod);
            if (current > 0) {
                concurrentCount.put(pod, current - 1);
            }
        }
    }
}
