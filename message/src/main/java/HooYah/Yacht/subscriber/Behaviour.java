package HooYah.Yacht.subscriber;

import HooYah.Yacht.Topic;
import HooYah.Yacht.dto.BasedEventData;
import java.util.HashMap;
import java.util.Map;

public interface Behaviour<T extends BasedEventData> {
    void subscribe(T message);

    static SubscribeBehaviourBuilder builder() {
        return new SubscribeBehaviourBuilder();
    }

    class SubscribeBehaviourBuilder {

        private final Map<Topic, Behaviour<? extends BasedEventData>> subscribeBehaviours = new HashMap<>();

        private SubscribeBehaviourBuilder() {}

        public SubscribeBehaviourBuilder add(Topic topic, Behaviour<? extends BasedEventData> behaviour) {
            subscribeBehaviours.put(topic, behaviour);
            return this;
        }

        public Map<Topic, Behaviour<? extends BasedEventData>> build() {
            return subscribeBehaviours;
        }

    }
}
