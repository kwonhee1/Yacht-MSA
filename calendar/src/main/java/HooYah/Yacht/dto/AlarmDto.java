package HooYah.Yacht.dto;

import HooYah.Yacht.domain.Alarm;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class AlarmDto {

    private Object part;
    private OffsetDateTime date;

    public static AlarmDto of(Alarm alarm, Object partInfo) {
        AlarmDto alarmDto = new AlarmDto();
        alarmDto.date = alarm.getDate();
        alarmDto.part = partInfo;
        return alarmDto;
    }

    public static List<AlarmDto> list (List<Alarm> alarmList, List partInfoList) {
        List<AlarmDto> alarmDtoList = new ArrayList<>(alarmList.size());
        for(int i = 0; i < alarmList.size(); i++)
            alarmDtoList.add(AlarmDto.of(alarmList.get(i), partInfoList.get(i)));

        return alarmDtoList;
    }

}
