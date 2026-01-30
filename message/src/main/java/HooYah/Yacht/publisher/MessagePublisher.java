package HooYah.Yacht.publisher;

import HooYah.Yacht.dto.BasedEventData;

public interface MessagePublisher <T extends BasedEventData> {

    void publish(T message);

}
