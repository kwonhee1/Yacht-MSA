package HooYah.Yacht;

import HooYah.Yacht.domain.Part;
import HooYah.Yacht.domain.Repair;
import HooYah.Yacht.dto.part.UpdatePartDto;
import java.time.OffsetDateTime;

public class TestUtil {

    public static Part generatePart(Long pk, Long yachtId) {
        return Part.builder()
                .yachtId(yachtId)
                .id(pk)
                .name("part name")
                .model("part model")
                .manufacturer("part manufacturer")
                .interval(3L)
                .build();
    }

    public static Repair generateRepair(Long pk, Part part, Long userId) {
        return Repair.builder()
                .content("repair content")
                .id(pk)
                .part(part)
                .userId(userId)
                .repairDate(OffsetDateTime.now())
                .build();
    }

    public static UpdatePartDto generateUpdatePartDto(String partName, Long partInterval) {
        UpdatePartDto updatePartDto = new UpdatePartDto();
        updatePartDto.setName(partName);
        updatePartDto.setInterval(partInterval);
        return updatePartDto;
    }

}
