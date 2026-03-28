package HooYah.Yacht.subscriber;

import HooYah.Yacht.Topic;
import HooYah.Yacht.event.BasedEvent;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter
public class SubscribeBehaviour <E extends BasedEvent> {

    private Class<E> receiveEventClass;
    private Behaviour<E> behaviour;

    public SubscribeBehaviour(Class<E> receiveEventClass, Behaviour<E> behaviour) {
        this.receiveEventClass = receiveEventClass;
        this.behaviour = behaviour;
    }

    public static SubscribeBehaviourBuilder builder() {
        return new SubscribeBehaviourBuilder();
    }

    public interface Behaviour <E extends BasedEvent> {
        void subscribe(E message);
    }

    public static class SubscribeBehaviourBuilder {

        private final Map<Topic, SubscribeBehaviour<? extends BasedEvent>> subscribeBehaviours = new HashMap<>();

        private SubscribeBehaviourBuilder() {}

        public <E extends BasedEvent> SubscribeBehaviourBuilder add (
                Topic topic,
                Class<E> receiveEventClass,
                Behaviour<E> behaviour
        ) {
            subscribeBehaviours.put(topic, new SubscribeBehaviour<>(receiveEventClass, behaviour));
            return this;
        }

        public Map<Topic, SubscribeBehaviour<? extends BasedEvent>> build() {
            return subscribeBehaviours;
        }

    }

}
