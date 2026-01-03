package HooYah.Yacht.part.domain;

import HooYah.Yacht.repair.domain.Repair;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "part")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Part {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long yachtId;

    @Column
    private String name;
    @Column
    private String manufacturer;
    @Column
    private String model;
    @Column(name = "`interval`")
    private Long interval; // 몇달

    @OneToMany(mappedBy = "part", cascade = CascadeType.REMOVE)
    private List<Repair> repairs;

    public void update(String name, String manufacturer, String model) {
        this.name = name;
        this.manufacturer = manufacturer;
        this.model = model;
    }

    public void updateInterval(Long interval) {
        this.interval = interval;
    }

    public OffsetDateTime nextRepairDate(OffsetDateTime oldRepairDate) {
        return oldRepairDate.plusMonths(interval);
    }

}
