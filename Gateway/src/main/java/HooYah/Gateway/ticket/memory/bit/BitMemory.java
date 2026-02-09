package HooYah.Gateway.ticket.memory.bit;

import HooYah.Gateway.ticket.memory.Memory;
import HooYah.Gateway.ticket.memory.NoValueException;
import HooYah.Gateway.ticket.uuid.vo.UUID;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class BitMemory implements Memory<UUID> {

    private static final long TTL_SECOND = 30L;

    private final BitManager bitManager;

    private final AtomicInteger nextIssueId = new AtomicInteger(1);
    private final AtomicInteger nextCheckId = new AtomicInteger(1); // 이 값까지는 더이상 1이 없음 --> 뭔 개소리야

    private final Map<UUID, Integer> map = new ConcurrentHashMap<>();
    private final Map<Integer, LocalDateTime> lastSelect = new ConcurrentHashMap<>();
    // 둘중 한개만 책임을 지자 --> 그게 맞음 누가 책임 질까? gpt가 책임 지지 않을까?

    private final Queue<Remind> reminder = new ConcurrentLinkedQueue();

    private class Remind {
        private static final long LEAST_SELECT_SECOND = 30L;

        private final LocalDateTime publishTime;
        private final int id;
        private final UUID uuid;

        private Remind(LocalDateTime publishTime, int id, UUID uuid) {
            this.publishTime = publishTime;
            this.id = id;
            this.uuid = uuid;
        }
        private LocalDateTime getReminderTime() {
            return publishTime.plusSeconds(LEAST_SELECT_SECOND);
        }
    }

    public BitMemory(int size) {
        bitManager = new BitManager(size);
    }

    @Override
    public int put(UUID key) {
        int id;
        do {
            id = nextIssueId.get();
            bitManager.set(id, true);
        } while (!nextIssueId.compareAndSet(id, id + 1));

        map.put(key, id);
        lastSelect.put(id, LocalDateTime.now());

        return bitManager.count(nextCheckId.get(), id);
    }

    @Override
    public int refreshValue(UUID key) {
        int id = getValue(key);

        if(!validateLastSelect(lastSelect.get(id))) {
            expirePassedItem(id);
            throw new AlreadyPassedException(id);
        }

        return bitManager.count(nextCheckId.get(), id);
    }

    @Override
    public int updateKey(UUID oldKey, UUID newKey) {
        int id = getValue(oldKey);

        if(!validateLastSelect(lastSelect.get(id))) {
            expirePassedItem(id);
            throw new AlreadyPassedException(id);
        }

        lastSelect.put(id, LocalDateTime.now());

        map.remove(oldKey);
        map.put(newKey, id);

        return bitManager.count(nextCheckId.get(), id);
    }

    @Override
    public void pop(UUID key) {
        int id = getValue(key);

        if(!validateLastSelect(lastSelect.get(id))) {
            expirePassedItem(id);
            throw new AlreadyPassedException(id);
        }

        bitManager.set(id, false);

        lastSelect.remove(id);
        map.remove(key);

        expirePassedItems();
    }

    private void expirePassedItems() {
        Remind head;
        while((head = reminder.peek()) != null) {
            if(head.publishTime.isAfter(LocalDateTime.now()))
                return;

            int id = head.id;
            LocalDateTime lastSelectTime = lastSelect.get(id);

            if(!validateLastSelect(lastSelectTime)) {
                // is zombi
                bitManager.set(id, false);
                map.remove(head.uuid);
                lastSelect.remove(id);
            }

            reminder.remove(head);
        }
    }

    private int getValue(UUID uuid) {
        Integer value = map.get(uuid);
        if(value == null)
            throw new NoValueException(uuid);
        return value;
    }

    private void expirePassedItem(int id) {
        bitManager.set(id, false);
        lastSelect.remove(id);
    }

    private boolean validateLastSelect(LocalDateTime lastSelectTime) {
        return !(lastSelectTime == null || lastSelectTime.isBefore(LocalDateTime.now().minusSeconds(TTL_SECOND)));
    }

}
