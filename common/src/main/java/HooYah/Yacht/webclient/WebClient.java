package HooYah.Yacht.webclient;

import HooYah.Yacht.excetion.CustomException;
import HooYah.Yacht.excetion.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

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

    public Object webClient(String uri, HttpMethod method, Object body) {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .header("Content-Type", "application/json");

        if(method.hasBody)
            requestBuilder.method(method.name(), BodyPublishers.ofString(Mapper.toString(body)));
        else
            requestBuilder.method(method.name(), BodyPublishers.noBody());

        HttpRequest request = requestBuilder.build();
        return send(request);
    }

    private Object send (HttpRequest request) {
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


        Map response = Mapper.toMap(httpResponse.body());
        return response.get("response");
    }

    class Mapper {
        private static ObjectMapper objectMapper;

        private static void setObjectMapper(TimeZone timeZone) {
            objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.setTimeZone(timeZone.getTimeZone());
        }

        public static String toString(Object value) {
            try {
                return objectMapper.writeValueAsString(value);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        public static <T> T toObject(String value, Class<T> clazz) {
            try {
                return objectMapper.readValue(value, clazz);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        public static Map toMap(String value) {
            return toObject(value, Map.class);
        }

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
