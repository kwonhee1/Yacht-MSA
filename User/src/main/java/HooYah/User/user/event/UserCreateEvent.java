package HooYah.User.user.event;

import HooYah.Yacht.event.BasedEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Getter
@ToString
public class UserCreateEvent extends BasedEvent {
    private String token;

    public UserCreateEvent(Long id, String token) {
        super(id, id);
        this.token = token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
