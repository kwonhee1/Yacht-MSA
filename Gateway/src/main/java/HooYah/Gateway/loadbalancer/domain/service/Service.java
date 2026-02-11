package HooYah.Gateway.loadbalancer.domain.service;

import HooYah.Gateway.loadbalancer.domain.pod.Pod;
import HooYah.Gateway.loadbalancer.domain.vo.Uri;
import java.util.List;

// module: uri
public class Service {

    private final List<Uri> uriList;
    private List<Pod> pods;

    private List<Pod> subPods;

    public Service(List<Uri> uriList, List<Pod> pods, List<Pod> subPods) {
        this.uriList = uriList;
        this.pods = pods;
        this.subPods = subPods;
    }

    public boolean matches(Uri requestUri) {
        for(Uri uri : uriList)
            if(uri.isMatch(requestUri))
                return true;

        return false;
    }

    public List<Pod> getPods() {
        return pods;
    }

    @Override
    public String toString() {
        return "Module{" +
                "uri=" + uriList +
                ", pods=" + pods +
                ", subPods=" + subPods +
                '}';
    }
}
