package HooYah.Gateway.ticket.bit;

import static java.util.Collections.synchronizedList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import HooYah.Gateway.ticket.memory.NoValueException;
import HooYah.Gateway.ticket.memory.bit.BitMemory;
import HooYah.Gateway.ticket.uuid.util.UUIDUtil;
import HooYah.Gateway.ticket.uuid.vo.UUID;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class BitMemoryTest {

    private BitMemory bitMemory;

    private int threadCount = 1000;
    ExecutorService executorService;
    CountDownLatch latch;

    @BeforeEach
    public void init() {
        executorService = Executors.newFixedThreadPool(64);
        bitMemory = new BitMemory(1000);
        latch = new CountDownLatch(threadCount);
    }

    @Test
    public void putTest() {
        UUID[] uuids = new UUID[10];
        for (int i = 0; i < uuids.length; i++) {
            uuids[i] = UUIDUtil.generateUUID();
            bitMemory.put(uuids[i]);
        }

        for(int i = 0; i < uuids.length; i++) {
            Assertions.assertEquals(i+1, bitMemory.refreshValue(uuids[i]));
        }
    }

    @Test
    @DisplayName("put 함수의 동시성 test")
    public void putTestInMultiThread() throws InterruptedException {
        List<Integer> results = synchronizedList(new ArrayList<>());

        for(int i = 0; i < threadCount; i++) {
            executorService.execute(() -> {
                try {
                    results.add(bitMemory.put(UUIDUtil.generateUUID()));
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        assertEquals(threadCount, results.size());
        assertEquals(threadCount, results.stream().distinct().count());
    }

    @Test
    @DisplayName("muti thread에서 update, refresh를 해도 값이 섞이지 않음")
    public void updateKeyTestInMultiThread() throws InterruptedException {
        List<UUID> uuidList = synchronizedList(new ArrayList<>());
        AtomicInteger atomicInteger = new AtomicInteger(0);

        for(int i = 0; i < threadCount; i++) {
            UUID uuid =  UUIDUtil.generateUUID();
            bitMemory.put(uuid);
            uuidList.add(uuid);
        }

        for(int j = 0; j < threadCount; j++) {
            executorService.execute(() -> {
                try {
                    int i = atomicInteger.getAndIncrement();

                    UUID oldUUID = uuidList.get(i);
                    UUID newUUID = UUIDUtil.generateUUID();

                    bitMemory.refreshValue(oldUUID);
                    bitMemory.updateKey(oldUUID, newUUID);

                    uuidList.set(i, newUUID);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        for(int j = 0; j < threadCount; j++) {
            Assertions.assertEquals(j+1, bitMemory.refreshValue(uuidList.get(j)));
        }
    }

    @Test
    @DisplayName("위에서 부터 pop하면 다음은 1번이 됨")
    public void popTest() {
        List<UUID> uuidList = new ArrayList<>();
        for(int i = 0; i < threadCount; i++) {
            UUID uuid = UUIDUtil.generateUUID();
            uuidList.add(uuid);
            bitMemory.put(uuid);
        }

        for(int i = 0; i < threadCount; i++) {
            UUID uuid = uuidList.get(i);
            Assertions.assertEquals(1, bitMemory.refreshValue(uuid));
            bitMemory.pop(uuid);
        }
    }

    @Test
    @DisplayName("muti thread 환경에서 pop해도 모두 pop 되어야 합니다")
    public void popTestInMultiThread() throws InterruptedException {
        List<UUID> uuidList = synchronizedList(new ArrayList<>());
        AtomicInteger atomicInteger = new AtomicInteger(0);

        for(int i = 0; i < threadCount; i++) {
            UUID uuid =  UUIDUtil.generateUUID();
            bitMemory.put(uuid);
            uuidList.add(uuid);
        }

        for(int j = 0; j < threadCount; j++) {
            executorService.execute(() -> {
                try {
                    int id = atomicInteger.getAndIncrement();
                    bitMemory.pop(uuidList.get(id));
                } finally {
                    latch.countDown();
                }
            });
        }

        AtomicInteger atomicInteger1 = new AtomicInteger(0);
        for(int i = 0; i < threadCount; i++) {
            Assertions.assertThrows(
                    NoValueException.class,
                    ()->bitMemory.refreshValue(uuidList.get(atomicInteger1.getAndIncrement())));
        }
    }

//    @Test
//    @DisplayName("좀비를 잘 잡아내는 지 확인 필요")
//    public void expirePassedItemsTest() throws NoSuchFieldException {
//        LastSelect zombiSelect = Mockito.mock(LastSelect.class);
//        Mockito.lenient().when(zombiSelect.validate()).thenReturn(false);
//
//
//        Field lastSelectField = BitMemory.class.getDeclaredField("lastSelect");
//        lastSelectField.setAccessible(true);
//
//        // 1 z z 1 z z 1
//        List<UUID> uuidList = new ArrayList<>();
//        for(int i = 0; i < 7; i++) {
//            uuidList.add(UUIDUtil.generateUUID());
//            bitMemory.put(uuidList.get(i));
//        }
//
//        Map<Integer, LastSelect> lastSelect = (Map<Integer, LastSelect>)lastSelectField.get(bitMemory);
//        lastSelect.put(1, zombiSelect);
//        lastSelect.put(2, zombiSelect);
//        lastSelect.put(4, zombiSelect);
//        lastSelect.put(5, zombiSelect);
//
//        Assertions.assertEquals(1, bitMemory.refreshValue(uuidList.get(0)));
//        Assertions.assertEquals(2, bitMemory.refreshValue(uuidList.get(1)));
//    }

}
