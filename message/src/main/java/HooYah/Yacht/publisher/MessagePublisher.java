package HooYah.Yacht.publisher;

import HooYah.Yacht.event.BasedEvent;

public interface MessagePublisher <T extends BasedEvent> {

    void publish(T message);

}
