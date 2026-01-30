package HooYah.Yacht;

public class WrongEventClassException extends RuntimeException {
    public WrongEventClassException(Topic topic, Class wrongEventClass) {
        super(String.format("Wrong event class expected::%s but::%s", topic.eventClass(), wrongEventClass));
    }
}
