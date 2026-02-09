package HooYah.Gateway.ticket.memory;

import HooYah.Gateway.ticket.uuid.vo.UUID;

public interface Memory <K> {

    int put(UUID key);
    int refreshValue(UUID key);

    int updateKey(K oldKey, K newKey);

    void pop(K key);

}
