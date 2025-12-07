package HooYah.YachtUser.user.dto.response;

import HooYah.YachtUser.user.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class UserInfoDto {

    private String email;
    private String name;

    public static UserInfoDto of(User user) {
        if(user == null) return new UserInfoDto();

        UserInfoDto dto = new UserInfoDto();
        dto.email = user.getEmail();
        dto.name = user.getName();
        return dto;
    }

}
