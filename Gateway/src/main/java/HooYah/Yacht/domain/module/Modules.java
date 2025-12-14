package HooYah.Yacht.domain.module;

import HooYah.Yacht.domain.vo.Host;
import HooYah.Yacht.domain.vo.Uri;
import HooYah.Yacht.domain.vo.Url;
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
