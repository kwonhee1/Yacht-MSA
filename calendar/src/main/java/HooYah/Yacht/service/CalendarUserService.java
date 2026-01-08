package HooYah.Yacht.service;

import HooYah.Redis.RedisService;
import HooYah.Yacht.domain.Calendar;
import HooYah.Yacht.domain.CalendarUser;
import HooYah.Yacht.excetion.CustomException;
import HooYah.Yacht.excetion.ErrorCode;
import HooYah.Yacht.webclient.WebClient;
import HooYah.Yacht.webclient.WebClient.HttpMethod;
import java.util.HashSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
// validate 는 밖에서, add 하는 로직만 여기서 (정말 add, delete, update 함수만 정의 할 것!)
public class CalendarUserService {

    private final WebClient webClient;
    private final RedisService userRedisService;

    @Value("${web-client.gateway}")
    private String gatewayURL;
    @Value("${web-client.user-list}")
    private String userListURI;

    /**
     * add YachtUser List to Calendar
     *
     * @param calendar   target calendar
     * @param userIdList  input userIdList, must be verified!
     * @return void
     */
    public void addUser(
            Calendar calendar,
            List<Long> userIdList // must be verified!
    ) {
        List<CalendarUser> calendarUserList = userIdList
                .stream()
                .map(userId->CalendarUser.builder().calendar(calendar).userId(userId).build())
                .toList();

        calendar.setCalendarUsers(calendarUserList);
    }

}
