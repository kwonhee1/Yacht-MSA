package HooYah.Gateway.domain.module;

import HooYah.Gateway.domain.service.Service;
import HooYah.Gateway.domain.vo.Uri;
import java.util.List;

// module: uri
public class Module {

    private final List<Uri> uriList;
    private List<Service> services;

    private List<Service> subServices;

    private ModuleStatus moduleStatus; // scale out, scale in을 결정함

    public Module(List<Uri> uriList, List<Service> services, List<Service> subServices) {
        this.uriList = uriList;
        this.services = services;
        this.subServices = subServices;
    }

    public boolean matches(Uri requestUri) {
        for(Uri uri : uriList)
            if(uri.isMatch(requestUri))
                return true;

        return false;
    }

    public List<Service> getServices() {
        return services;
    }

    @Override
    public String toString() {
        return "Module{" +
                "uri=" + uriList +
                ", services=" + services +
                ", subServices=" + subServices +
                ", moduleStatus=" + moduleStatus +
                '}';
    }
}
