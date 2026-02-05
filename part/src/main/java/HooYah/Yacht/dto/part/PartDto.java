package HooYah.Yacht.dto.part;

import HooYah.Yacht.domain.Part;
import HooYah.Yacht.domain.Repair;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@NoArgsConstructor
@Getter
@Setter
public class PartDto {

    private Long id;
    private String name;
    private String manufacturer;
    private String model;
    private Long interval;

    private OffsetDateTime lastRepair;

    public static PartDto of(Part part, Repair lastRepair) {
        PartDto partDto = new PartDto();
        partDto.id = part.getId();
        partDto.name = part.getName();
        partDto.manufacturer = part.getManufacturer();
        partDto.model = part.getModel();
        partDto.interval = part.getInterval();
        partDto.lastRepair = lastRepair!=null ? lastRepair.getRepairDate() : null;
        return partDto;
    }

    @Builder
    public PartDto(String name, String manufacturer, String model, Long interval) {
        this.name = name;
        this.manufacturer = manufacturer;
        this.model = model;
        this.interval = interval;
    }
}
