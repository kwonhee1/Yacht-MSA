package HooYah.Yacht.dto.repair;

import HooYah.Yacht.domain.Repair;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@NoArgsConstructor
@Getter
@Setter
public class RepairDto {

    private Long id;
    private OffsetDateTime repairDate;
    private String content;

    private Object user;

    public static RepairDto of(Repair repair, Object userInfo) {
        RepairDto repairDto = new RepairDto();
        repairDto.id = repair.getId();
        repairDto.repairDate = repair.getRepairDate();
        repairDto.content = repair.getContent() != null ?  repair.getContent() : "";
        repairDto.user = userInfo;
        return repairDto;
    }

}
