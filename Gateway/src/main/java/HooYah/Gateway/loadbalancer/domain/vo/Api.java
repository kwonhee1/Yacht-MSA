package HooYah.Gateway.loadbalancer.domain.vo;

public class Api {

    private String httpMethod; // GET, POST, PUT, DELETE, etc.
    private String bodyType; // JSON, XML, etc. (optional, nullable)
    private final Url url; // <protocol>://<host>:<port>/<uri>

    public Api(Url url) {
        this.url = url;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getBodyType() {
        return bodyType;
    }

    public Url getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return "Api{" +
                "httpMethod='" + httpMethod + '\'' +
                ", bodyType='" + bodyType + '\'' +
                ", url=" + url +
                '}';
    }
}
