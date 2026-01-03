package HooYah.Yacht.redis;

import HooYah.Redis.RedisService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final RedisService userRedisService;

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public List getUserInfo(List<Long> userIdList) {
        return userRedisService.getListOrSelect(userIdList, ()->askUserToUserServer(userIdList));
    }

    private List askUserToUserServer(List<Long> userIdList) {
        String uri = "http://localhost:8080/user/proxy/user-list";

        try {
            String body = objectMapper.writeValueAsString(userIdList);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(uri))
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofString(body))
                    .build();
            HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            List response = objectMapper.readValue(httpResponse.body(), List.class);

            return response;
        } catch (JsonProcessingException e) {
            // json error
            throw new RuntimeException(e);
        } catch (IOException |InterruptedException e) {
            // http client error
            throw new RuntimeException(e);
        }
    }

}
