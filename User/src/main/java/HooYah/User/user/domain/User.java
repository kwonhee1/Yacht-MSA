package HooYah.User.user.domain;

import HooYah.User.passwordencoder.PasswordEncoder;
import HooYah.Yacht.domain.BaseEntity;
import HooYah.Yacht.excetion.CustomException;
import HooYah.Yacht.excetion.ErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SQLDelete(sql = "UPDATE user SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String email;

    @Column
    private String password;

    @Column
    private String name;

    public void updatePassword(String newPassword, PasswordEncoder passwordEncoder) {
        if(passwordEncoder.matches(newPassword, password)) {}
            this.password = newPassword;
    }

    public void login(String email, String password,  PasswordEncoder passwordEncoder) {
        if(passwordEncoder.matches(password, this.password) && email.equals(this.email))
            return;

        throw new CustomException(ErrorCode.BAD_REQUEST);
    }

    @Builder
    public User(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

}
