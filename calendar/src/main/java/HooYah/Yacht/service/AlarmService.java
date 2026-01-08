package HooYah.Yacht.service;

import HooYah.Yacht.domain.Alarm;
import HooYah.Yacht.dto.AlarmDto;
import HooYah.Yacht.excetion.CustomException;
import HooYah.Yacht.excetion.ErrorCode;
import HooYah.Yacht.repository.AlarmRepository;
import HooYah.Yacht.webclient.WebClient;
import HooYah.Yacht.webclient.WebClient.HttpMethod;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AlarmService {

    private final AlarmRepository alarmRepository;
    private final FCMService fCMService;

    private final AskService askService;
    private final WebClient webClient;

    @Value("${web-client.gateway}")
    private String gatewayURL;

    @Value("${web-client.part-list-name}")
    private String partListNameURI;

    @Value("${web-client.yacht-user-list}")
    private String yachtUserListURI;

    @Value("${web-client.user-token-list}")
    private String userTokenListURI;

    @Deprecated
    public void createAlarm() {
        // can not create, update, delete alarm by user!
    }

    public List<AlarmDto> getAlarmList(Long userId) {
        List<Long> yachtList = askService.yachtListInMemory(userId);

        List<Alarm> alarmList = alarmRepository.findAllByYachtIds(yachtList, OffsetDateTime.now().plusWeeks(1));
        List<?> partInfoList = askService.getPartInfoList(alarmList.stream().map(Alarm::getPartId).toList());

        return AlarmDto.list(alarmList, partInfoList);
    }

    // "{part name}의 예상 정비일은 {날짜} 입니다!"
    @Transactional
    public void sendAlarm() {
        List<Alarm> alarmList = getAlarms();

        List<String> PartNameList = getAllPartName(alarmList.stream().map(Alarm::getPartId).toList());
        List<List<String>> totalUserTokenList = getAllUserToken(alarmList.stream().map(Alarm::getYachtId).toList());

        for(int i = 0; i < alarmList.size(); i++) {
            String message = PartNameList.get(i) + "의 예상 정비일은 " + alarmList.get(i).getDate() + " 입니다";

            fCMService.sendAll(message, totalUserTokenList.get(i));
        }
    }

    private List<String> getAllPartName(List<Long> partIdList) {
        // ask part domain
        List<String> allPartNameList = (List<String>) webClient.webClient(
                gatewayURL + partListNameURI, 
                HttpMethod.POST, 
                partIdList
        );
        if(allPartNameList == null || allPartNameList.size() != partIdList.size())
            throw new CustomException(ErrorCode.API_FAIL, "");
        return allPartNameList;
    }

    private List<List<String>> getAllUserToken(List<Long> yachtIdList) {
        // List<YachtId> -> List<List<UserId>> :: ask yacht domain
        List<List<Long>> userIdList = (List<List<Long>>) webClient.webClient(
                gatewayURL + yachtUserListURI, 
                HttpMethod.POST, 
                yachtIdList
        );
        if(userIdList == null || userIdList.size() != yachtIdList.size())
            throw new CustomException(ErrorCode.API_FAIL, "");

        // List<List<UserId>> -> List<List<String::userToken>> :: ask user domain
        List<Long> flatUserIdList = new ArrayList<>();
        for(List<Long> userIds : userIdList) {
            flatUserIdList.addAll(userIds);
        }

        List<String> flatUserTokenList = (List<String>) webClient.webClient(
                gatewayURL + userTokenListURI, 
                HttpMethod.POST, 
                flatUserIdList
        );
        if(flatUserTokenList == null || flatUserTokenList.size() != flatUserIdList.size())
            throw new CustomException(ErrorCode.API_FAIL, "");

        // List<String> -> List<List<String>> (reconstruct using subList)
        List<List<String>> userTokenList = new ArrayList<>(userIdList.size());
        int startIndex = 0;
        for(int i = 0; i < userIdList.size(); i++) {
            int endIndex = startIndex + userIdList.get(i).size();
            userTokenList.add(i, flatUserTokenList.subList(startIndex, endIndex));
            startIndex = endIndex;
        }

        return userTokenList;
    }

    private List<Alarm> getAlarms() {
        OffsetDateTime now = OffsetDateTime.now();

        OffsetDateTime todayStart = now.toLocalDate().atStartOfDay().atOffset(now.getOffset());
        OffsetDateTime todayEnd   = todayStart.plusDays(1);

        OffsetDateTime oneDayStart = todayStart.plusDays(1);
        OffsetDateTime oneDayEnd   = oneDayStart.plusDays(1);

        OffsetDateTime oneWeekStart = todayStart.plusDays(7);
        OffsetDateTime oneWeekEnd   = oneWeekStart.plusDays(1);

        return alarmRepository.findAllByDate(todayStart, todayEnd, oneDayStart, oneDayEnd, oneWeekStart, oneWeekEnd);
    }

}
