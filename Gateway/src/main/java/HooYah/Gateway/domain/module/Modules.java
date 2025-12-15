package HooYah.Gateway.domain.module;

import HooYah.Gateway.domain.vo.Uri;
import HooYah.Gateway.domain.vo.Url;
import java.util.List;

public class Modules {

    private List<Module> modules;

    public Modules(List<Module> modules) {
        this.modules = modules;
    }

    public Url loadRequest(Uri requestUri) {
        for(Module module : modules) {
            if(module.matches(requestUri)) {
                return module.loadRequest();
            }
        }

        throw new IllegalUriException(requestUri);
    }

}
