package HooYah.Yacht.subscriber;

import HooYah.Yacht.Topic;
import HooYah.Yacht.event.BasedEvent;
import java.util.HashMap;
import java.util.Map;

public interface Behaviour<T extends BasedEvent> {
    void subscribe(T message);

    static SubscribeBehaviourBuilder builder() {
        return new SubscribeBehaviourBuilder();
    }

    class SubscribeBehaviourBuilder {

        private final Map<Topic, Behaviour<? extends BasedEvent>> subscribeBehaviours = new HashMap<>();

        private SubscribeBehaviourBuilder() {}

        public SubscribeBehaviourBuilder add(Topic topic, Behaviour<? extends BasedEvent> behaviour) {
            subscribeBehaviours.put(topic, behaviour);
            return this;
        }

        public Map<Topic, Behaviour<? extends BasedEvent>> build() {
            return subscribeBehaviours;
        }

    }
}
