package HooYah.Yacht.publisher;

import HooYah.Yacht.Topic;
import HooYah.Yacht.dto.BasedEventData;
import java.util.logging.Logger;

public class LogStatePublisher implements MessagePublisher {

    private Logger logger = Logger.getLogger(LogStatePublisher.class.getName());
    private Topic topic;

    public LogStatePublisher(Topic topic) {
        this.topic = topic;

        logger.warning("before Redis Publisher ready, LogPublisher is running");
    }

    @Override
    public void publish(BasedEventData message) {
        logger.warning("Redis Is Not Ready Publish topic "+topic.topic()+" Log :: " + message.toString());
    }

}
