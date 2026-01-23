package HooYah.User.user.domain;

import HooYah.User.passwordencoder.PasswordEncoder;
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

@Entity
@Table(name = "user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String email;

    @Column
    private String password;

    @Column
    private String name;

    @Column
    private String token;

    public void updatePassword(String newPassword, PasswordEncoder passwordEncoder) {
        if(passwordEncoder.matches(newPassword, password)) {}
            this.password = newPassword;
    }

    public void login(String email, String password,  PasswordEncoder passwordEncoder) {
        if(passwordEncoder.matches(password, this.password) && email.equals(this.email))
            return;

        throw new CustomException(ErrorCode.BAD_REQUEST);
    }

    public void setToken(String token) {
        if(token != null)
            this.token = token;
    }

    @Builder
    public User(String email, String password, String name, String token) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.token = token;
    }

}
