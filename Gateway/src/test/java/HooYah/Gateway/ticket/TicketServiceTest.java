package HooYah.Gateway.ticket;

import HooYah.Gateway.ticket.dto.Ticket;
import HooYah.Gateway.ticket.service.TicketService;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TicketServiceTest {

    private TicketService ticketService;
    private final long MAX_ENTER_COUNT = 3L;

    @BeforeEach
    void setUp() {
        ticketService = new TicketService(MAX_ENTER_COUNT);
    }

    @Test
    @DisplayName("multi thread 환경에서 ticket을 발급 받아도 중복된 waiting number를 가진 ticket이 발급되지 않는다")
    void issueTicket_concurrent() throws InterruptedException {
        ticketService = new TicketService(0L); // 중간에 아무도 enter 로직을 실행하지 않아야함

        // given
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);
        List<Ticket> tickets = java.util.Collections.synchronizedList(new ArrayList<>());

        // when
        for (int i = 0; i < threadCount; i++) {
            executorService.execute(() -> {
                try {
                    tickets.add(ticketService.issueTicket());
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        // then
        List<Integer> waitingNumbers = tickets.stream().map(Ticket::getWaitingNumber).toList();
        assertEquals(threadCount, waitingNumbers.size());
        assertEquals(threadCount, waitingNumbers.stream().distinct().count());
    }

    @Test
    @DisplayName("수용량이 남아있을 때 티켓을 발급하면 입장 ticket을 발급한다")
    void updateTicket_enter() {
        ticketService = new TicketService(MAX_ENTER_COUNT);

        // given
        Ticket ticket = ticketService.issueTicket();

        // then
        assertTrue(ticket.canEnter());
    }

    @Test
    @DisplayName("첫번 째 ticket을 release 하면 이후 ticket이 입장 ticket을 발급 받는다")
    void updateTicket_wait() {
        // given
        ticketService = new TicketService(1L);
        List<Ticket> tickets = new ArrayList<>();
        tickets.add(null); // index를 맞추기 위함

        for (int i = 1; i <= 10 + 2; i++)
            tickets.add(ticketService.issueTicket());

        // 2~10 ticket은 입장 ticket을 발급 받지 못한다
        for (int i = 2; i <= 10 + 2; i++)
            Assertions.assertFalse(tickets.get(i).canEnter());

        // when
        ticketService.release(tickets.get(1));
        for(int i = 2; i <= 10 + 2; i++)
            tickets.set(i, ticketService.updateTicket(tickets.get(i).getUuid()));

        // 2번 ticket이 enter ticket, 나머지 not enter
        Assertions.assertTrue(tickets.get(2).canEnter());
        for (int i = 3; i <= 10 + 2; i++)
            Assertions.assertFalse(tickets.get(i).canEnter());
    }

    @Test
    @DisplayName("여러 request가 들어와도 동시 접속자는 n명 이내여야 한다")
    public void multiThreadTest() throws InterruptedException {
        int threadCount = 100;
        long limitConcurrentCount = 6;
        AtomicInteger currentRequestCount = new AtomicInteger(0);
        AtomicInteger maxConcurrentRequestCount = new AtomicInteger(0);

        ticketService = new TicketService(limitConcurrentCount);

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.execute(() -> {
                try {
                    Ticket ticket = ticketService.issueTicket();
                    while (!ticket.canEnter()) {
                        Thread.sleep(100L);
                        ticket = ticketService.updateTicket(ticket.getUuid());
                    }

                    synchronized (maxConcurrentRequestCount) {
                        maxConcurrentRequestCount.set(
                                Math.max(currentRequestCount.incrementAndGet(), maxConcurrentRequestCount.get()));
                    }

                    Thread.sleep(1000L);

                    currentRequestCount.decrementAndGet();
                    ticketService.release(ticket);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        System.out.println("maxConcurrentRequestCount: " + maxConcurrentRequestCount.get());
        Assertions.assertTrue(maxConcurrentRequestCount.get() <= limitConcurrentCount);
    }

    @Test
    @DisplayName("여러 request가 들어와도 동시 접속자는 설정된 6명 이내여야 한다")
    public void multiThreadTest2() throws InterruptedException {
        // 1. 환경 설정
        int threadCount = 100;
        long maxConcurrentLimit = 6;

        ticketService = new TicketService(maxConcurrentLimit);

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // 동시성을 측정하기 위한 원자적 변수들
        AtomicInteger currentActiveUsers = new AtomicInteger(0);
        AtomicInteger maxObservedConcurrency = new AtomicInteger(0);
        AtomicInteger successCount = new AtomicInteger(0);

        // 2. 100개의 요청 실행
        for (int i = 0; i < threadCount; i++) {
            executorService.execute(() -> {
                try {
                    // 티켓 발급
                    Ticket ticket = ticketService.issueTicket();

                    // 입장이 가능할 때까지 폴링 (Waiting Queue 모사)
                    while (!ticket.canEnter()) {
                        Thread.sleep(100L); // 0.1초마다 재시도
                        ticket = ticketService.updateTicket(ticket.getUuid());
                    }

                    // --- [임계 구역: 실제 입장 완료] ---
                    int active = currentActiveUsers.incrementAndGet();

                    // 최고 동시 접속자 수 갱신 (Race Condition 방지를 위해 반복 업데이트)
                    maxObservedConcurrency.updateAndGet(currentMax -> Math.max(currentMax, active));

                    // 실제 비즈니스 로직 수행 시간 (1초 대기)
                    Thread.sleep(1000L);

                    currentActiveUsers.decrementAndGet();
                    ticketService.release(ticket);
                    successCount.incrementAndGet();
                    // --- [임계 구역 종료] ---

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        // 3. 모든 스레드 종료 대기 (최대 30초)
        latch.await(30, TimeUnit.SECONDS);
        executorService.shutdown();

        // 4. 결과 검증
        System.out.println("최대 동시 접속자 수: " + maxObservedConcurrency.get());
        System.out.println("총 처리된 티켓 수: " + successCount.get());

        // 동시에 입장해 있던 인원이 설정된 limit(6)을 초과했는지 확인
        assertTrue(maxObservedConcurrency.get() <= maxConcurrentLimit,
                "동시 접속자가 제한 수치를 초과했습니다! 관측값: " + maxObservedConcurrency.get());

        assertEquals(threadCount, successCount.get(), "모든 티켓이 정상적으로 처리되지 않았습니다.");
    }

}