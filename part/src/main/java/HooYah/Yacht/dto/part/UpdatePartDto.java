package HooYah.Yacht.dto.part;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class UpdatePartDto {
    private Long id;
    private String name;
    private String manufacturer;
    private String model;
    private Long interval;
}
