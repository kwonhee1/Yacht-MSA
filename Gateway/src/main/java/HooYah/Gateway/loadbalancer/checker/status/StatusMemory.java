package HooYah.Gateway.loadbalancer.checker.status;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

// 지난 3개의 ServerStatus enum을 관리하기 위한 class
public class StatusMemory {

    private static final int MAX_SIZE = 5;
    private final Deque<ServiceStatus> buffer = new ArrayDeque<>(MAX_SIZE);

    public void addStatus(ServiceStatus status) {
        if (buffer.size() == MAX_SIZE) {
            buffer.removeFirst(); // 가장 오래된 값 제거
        }
        buffer.addLast(status);
    }

    public List<ServiceStatus> getAllStatus() {
        return new ArrayList<>(buffer); // 오래된 순 -> 최신 순
    }
}

