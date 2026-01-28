package HooYah.Yacht.webclient;

import HooYah.Yacht.excetion.CustomException;
import HooYah.Yacht.excetion.ErrorCode;
import HooYah.Yacht.webclient.config.TimeZone;
import HooYah.Yacht.webclient.response.WebResponse;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.time.Duration;

public class WebClient {

    private final int connectionTimeoutCount;

    public WebClient(TimeZone obejctMapperTimeZone, int connectionTimeoutCount) {
        Mapper.setObjectMapper(obejctMapperTimeZone);
        this.connectionTimeoutCount = connectionTimeoutCount;
    }

    public HttpClient createClient() {
        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(connectionTimeoutCount))
                .version(HttpClient.Version.HTTP_1_1)
                .build();
    }

    public WebResponse webClient(String uri, HttpMethod method, Object body) {
        HttpRequest request = buildHttpRequest(uri, method, body);
        return new WebResponse(send(request));
    }

    public void webClientAsync(String uri, HttpMethod method, Object body) {
        HttpRequest request = buildHttpRequest(uri, method, body);
        createClient().sendAsync(request, (info)->null);
    }

    private HttpRequest buildHttpRequest(String uri, HttpMethod method, Object body) {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .header("Content-Type", "application/json");

        if(method.hasBody)
            requestBuilder.method(method.name(), BodyPublishers.ofString(Mapper.toString(body)));
        else
            requestBuilder.method(method.name(), BodyPublishers.noBody());

        return requestBuilder.build();
    }

    private String send (HttpRequest request) {
        HttpClient httpClient = createClient();
        HttpResponse<String> httpResponse;

        try {
            httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException | IOException e) {
            // http client error
            throw new RuntimeException(e);
        }

        if(httpResponse.statusCode() != 200)
            throw new CustomException(ErrorCode.API_FAIL, httpResponse.body());

        return httpResponse.body();
    }

    public enum HttpMethod {
        GET ("GET", false),
        POST ("POST", true),
        PUT ("PUT", true),
        DELETE ("DELETE", false),
        PATCH ("PATCH", false);

        public final String method;
        private final boolean hasBody;

        HttpMethod(String method, boolean hasBody) {
            this.method = method;
            this.hasBody = hasBody;
        }

        public boolean hasBody() {
            return hasBody;
        }

    }

}
