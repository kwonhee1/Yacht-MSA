package HooYah.Yacht.yacht.domain;

import HooYah.Yacht.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "yacht")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE yacht SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
public class Yacht extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private String nickName;

    @Column(unique = true, nullable = false)
    @Builder.Default
    private String inviteCode = UUID.randomUUID().toString();

    @OneToMany(mappedBy = "yacht", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<YachtUser> yachtUser;

    public void updateName(String name) {
        if(name != null && !name.isEmpty())
            this.name = name;
    }

    public void updateNickName(String nickName) {
        if(nickName != null && !nickName.isEmpty())
            this.nickName = nickName;
    }

}
