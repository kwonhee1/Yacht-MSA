package HooYah.Gateway.loadbalancer.checker.status;

public enum ServiceStatus {
    GOOD,
    NORMAL,
    BAD, // need auto scaling
    DEAD,
    UNKNOWN;
}