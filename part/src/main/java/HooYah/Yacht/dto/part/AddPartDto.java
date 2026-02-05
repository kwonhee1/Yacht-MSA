package HooYah.Yacht.dto.part;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@NoArgsConstructor
@Getter
@Setter
public class AddPartDto {

    private Long yachtId;

    private String name;
    private String manufacturer;
    private String model;
    private Long interval;

    private OffsetDateTime lastRepair; // 마지막 정비 날짜, can null

}
