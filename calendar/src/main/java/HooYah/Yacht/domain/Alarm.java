package HooYah.Yacht.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "alarm")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Alarm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long partId;

    @Column
    private Long yachtId;

    @Column
    private OffsetDateTime date;

    //part 별 user 정보를 반정규화 해서 가진다면? user 가 token을 변경했을 때 누가 alarm을 수정할 것인가? --> message que로 Alarm이 직접 수정?
    // yachtId값만 가지고 있었더라면? -> 한결 편해짐 (List<partId> -> List<YachtId> 가 필요 없어짐)

}
