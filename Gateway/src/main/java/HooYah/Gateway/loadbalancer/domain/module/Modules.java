package HooYah.Gateway.loadbalancer.domain.module;

import HooYah.Gateway.loadbalancer.domain.vo.Uri;
import java.util.List;

public class Modules {

    private List<Module> modules;

    public Modules(List<Module> modules) {
        this.modules = modules;
    }

    public Module matching(Uri requestUri) {
        for(Module module : modules) {
            if(module.matches(requestUri)) {
                return module;
            }
        }

        throw new IllegalUriException(requestUri);
    }

}
