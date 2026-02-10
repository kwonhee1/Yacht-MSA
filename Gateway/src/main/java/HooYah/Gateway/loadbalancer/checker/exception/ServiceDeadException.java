package HooYah.Gateway.loadbalancer.checker.exception;

import HooYah.Gateway.loadbalancer.domain.service.Service;

public class ServiceDeadException extends RuntimeException {
    public ServiceDeadException(Service service) {
        super("Service " + service.getName() + " is dead");
    }
}
