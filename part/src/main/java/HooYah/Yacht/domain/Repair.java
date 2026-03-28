package HooYah.Yacht.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.OffsetDateTime;

@Entity
@Table(name = "repair")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE repair SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
public class Repair extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_id")
    private Part part;

    @Column
    private OffsetDateTime repairDate;

    @Column
    private String content;

    public boolean updateRepairDate(OffsetDateTime repairDate) {
        if(repairDate == null || repairDate.equals(this.repairDate))
            return false;
        else {
            this.repairDate = repairDate;
            return true;
        }
    }

    public void updateContent(String content) {
        if(content != null && !content.isEmpty())
            this.content = content;
    }

    public Long getPartId() {
        return part.getId();
    }

    @Override
    public boolean equals(Object o) {
        if (o.getClass() != Repair.class)
            return false;
        return ((Repair)o).id.equals(this.id);
    }

}
