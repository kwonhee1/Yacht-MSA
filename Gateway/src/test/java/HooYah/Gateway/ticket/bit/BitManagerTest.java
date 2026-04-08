package HooYah.Gateway.ticket.bit;

import HooYah.Gateway.ticket.memory.bit.BitManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BitManagerTest {
    private BitManager manager;
    private final int INITIAL_SIZE = 1000;

    @BeforeEach
    void setUp() {
        // 각 테스트 시작 전 매니저 초기화
        manager = new BitManager(INITIAL_SIZE);
    }

    @Test
    @DisplayName("특정 인덱스의 비트 설정 및 읽기 테스트")
    void testSetAndRead() {
        manager.set(10, true);
        manager.set(500, true);
        manager.set(999, true);

        assertTrue(manager.read(10), "10번 비트는 true여야 합니다.");
        assertTrue(manager.read(500), "500번 비트는 true여야 합니다.");
        assertTrue(manager.read(999), "999번 비트는 true여야 합니다.");

        assertFalse(manager.read(0), "설정하지 않은 0번 비트는 false여야 합니다.");
        assertFalse(manager.read(11), "설정하지 않은 11번 비트는 false여야 합니다.");
    }

    @Test
    @DisplayName("비트 상태를 true에서 false로 변경 테스트")
    void testClearBit() {
        manager.set(100, true);
        assertTrue(manager.read(100));

        manager.set(100, false); // 다시 끔
        assertFalse(manager.read(100), "다시 false로 설정하면 false를 반환해야 합니다.");
    }

    @Test
    @DisplayName("범위 내 1의 개수 세기(count) 테스트")
    void testCountRange() {
        // 10, 11, 12, 13, 14번에 비트 설정 (총 5개)
        for (int i = 10; i < 15; i++) {
            manager.set(i, true);
        }

        // 1. 전체 포함 범위
        assertEquals(5, manager.count(0, 20), "0~20 사이의 1은 5개여야 합니다.");

        // 2. 일부 포함 범위 (10, 11, 12, 13)
        assertEquals(4, manager.count(0, 13), "0~13 사이의 1은 3개여야 합니다.");

        // 3. 해당 없음 범위
        assertEquals(0, manager.count(15, 100), "15~100 사이의 1은 0개여야 합니다.");
    }

    @Test
    @Disabled // gemini가 짜줬는데 이게 가능할 리가 없잖아....
    @DisplayName("동적 확장 테스트 (초기 크기 초과 인덱스)")
    void testDynamicExpansion() {
        // 초기 크기 1000을 넘어서는 인덱스에 접근
        int largeIndex = 5000;
        assertDoesNotThrow(() -> manager.set(largeIndex, true));
        assertTrue(manager.read(largeIndex), "동적으로 확장되어 값을 저장할 수 있어야 합니다.");
    }
}