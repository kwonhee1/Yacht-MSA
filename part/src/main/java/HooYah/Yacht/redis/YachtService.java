package HooYah.Yacht.redis;

import HooYah.Redis.RedisService;
import HooYah.Yacht.common.excetion.CustomException;
import HooYah.Yacht.common.excetion.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class YachtService {

    private final RedisService yachtRedisService;

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public void validateYacht(Long yachtId) {
        Optional validateYacht = yachtRedisService.getOrSelect(yachtId, ()->
                askYachtToYachtServer(yachtId)
        );

        if(validateYacht.isEmpty())
            throw new CustomException(ErrorCode.NOT_FOUND);
    }

    private Optional askYachtToYachtServer(Long yachtId) {
        String uri = String.format("http://localhost:8443/yacht/%d", yachtId);

        try {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(uri))
                    .GET()
                    .build();
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            Map response = objectMapper.readValue(httpResponse.body(), Map.class);

            return Optional.of(response.get("response"));
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void validateYachtUser(Long yachtId, Long userId) {
        Optional validateUser = yachtRedisService.getOrSelect(yachtId, userId,
                // user domain server에게 web client
                ()->askYachtUserToYachtServer(yachtId, userId)
        );

        if(validateUser.isEmpty())
            throw new CustomException(ErrorCode.CONFLICT); // not yacht user!
    }

    // yacht domain에게 userId가 yacht의 User에 속하는지 확인
    private Optional askYachtUserToYachtServer(Long yachtId, Long userId) {
        String uri = String.format("http://localhost:8443/yacht/proxy/validate-user?yachtId=%d&userId=%d", yachtId, userId);

        try {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(uri))
                    .GET()
                    .build();
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if(httpResponse.statusCode() != 200)
                throw new CustomException(ErrorCode.PROXY_FAIL, httpResponse.body().toString());

            Map response = objectMapper.readValue(httpResponse.body(), Map.class);

            return Optional.of(response.get("response"));
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
