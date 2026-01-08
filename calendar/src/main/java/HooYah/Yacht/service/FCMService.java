package HooYah.Yacht.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FCMService {

    private static final String TITLE = "hooaah";

    /*
        @Param body     send Data
        @Param toList   to who, List<Long :: FCM Token>
     */
    public void sendAll(String body, List<String> toList) {
        try {
            List<Message> messages = toList
                    .stream()
                    .map(token -> makeMessage(body, token))
                    .toList();

            FirebaseMessaging.getInstance().sendAll(messages); // todo : 개별 error 처리 필요, 한개만 실패해도 모두 실패 가능성 확인 필요
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Message makeMessage(String token, String body) {
        return Message.builder()
                .setToken(token)
                .setNotification(
                        Notification.builder()
                                .setTitle(TITLE)
                                .setBody(body)
                                .build())
                .build();
    }
}
