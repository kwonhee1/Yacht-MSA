package HooYah.Yacht.event;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@ToString
public class CreateEvent extends BasedEvent<CreateEvent.Data> {

    public CreateEvent(Long id, String token) {
        super(id, id, new Data(token));
    }

    public String getToken() {
        return getData().token;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class Data {
        private String token;
    }

}
