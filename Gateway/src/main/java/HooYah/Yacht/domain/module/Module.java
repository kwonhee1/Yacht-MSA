package HooYah.Yacht.domain.module;

import HooYah.Yacht.domain.service.Service;
import HooYah.Yacht.domain.vo.Host;
import HooYah.Yacht.domain.vo.Uri;
import HooYah.Yacht.domain.vo.UriMatcher;
import HooYah.Yacht.domain.vo.Url;
import java.util.List;

public class Module {

    private UriMatcher matcher;
    private List<Service> services;

    private List<Service> subServices;

    private ModuleStatus moduleStatus; // scale out, scale in을 결정함

    public Module(UriMatcher matcher, List<Service> services, List<Service> subServices) {
        this.matcher = matcher;
        this.services = services;
        this.subServices = subServices;
    }

    public boolean matches(Uri requestUri) {
        return matcher.isMatch(requestUri);
    }

    public Url loadRequest() {
        return services.getFirst().getUrl();
    }

}
