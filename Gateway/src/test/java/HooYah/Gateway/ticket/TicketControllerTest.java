package HooYah.Gateway.ticket;

import HooYah.Gateway.ticket.dto.Ticket;
import HooYah.Gateway.ticket.service.TicketDomainEqual;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TicketControllerTest {

    private TicketController ticketController;
    private final long maxCurrentCount = 3L;

    private TicketDomainEqual ticketDomain = new TicketDomainEqual() {
        @Override
        public boolean equals(Object obj) {
            return true;
        }
    };

    @BeforeEach
    public void setUp() {
        ticketController = new TicketController();
        ticketController.register(ticketDomain, maxCurrentCount);
    }

    @Test
    @DisplayName("여러 request가 들어와도 동시 접속자는 n명 이내여야 한다")
    public void multiThreadTest() throws InterruptedException {
        int threadCount = 100;
        long limitConcurrentCount = 6;
        AtomicInteger currentRequestCount = new AtomicInteger(0);
        AtomicInteger maxConcurrentRequestCount = new AtomicInteger(0);

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.execute(() -> {
                try {
                    Ticket ticket = ticketController.issueTicket(ticketDomain);
                    while (!ticket.canEnter()) {
                        Thread.sleep(100L);
                        ticket = ticketController.updateTicket(ticketDomain, ticket.getUuid().toString());
                    }

                    synchronized (maxConcurrentRequestCount) {
                        maxConcurrentRequestCount.set(
                                Math.max(currentRequestCount.incrementAndGet(), maxConcurrentRequestCount.get()));
                    }

                    Thread.sleep(1000L);

                    currentRequestCount.decrementAndGet();
                    ticketController.release(ticketDomain, ticket);
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

}
