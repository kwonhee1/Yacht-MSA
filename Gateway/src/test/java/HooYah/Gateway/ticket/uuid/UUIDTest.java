package HooYah.Gateway.ticket.uuid;

import HooYah.Gateway.ticket.TicketController;
import HooYah.Gateway.ticket.dto.Ticket;
import HooYah.Gateway.ticket.service.TicketDomainEqual;
import HooYah.Gateway.ticket.uuid.util.UUIDUtil;
import HooYah.Gateway.ticket.uuid.vo.UUID;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UUIDTest {

    @Test
    public void UUIDEqualsTest() {
        UUID uuid1 = UUIDUtil.generateUUID();
        UUID uuid2 = new UUID(uuid1.getUuid().toString());

        Assertions.assertEquals(uuid1, uuid2);
    }

    @Test
    public void concurrentHashMapTest() {
        Map<UUID, Integer> map = new ConcurrentHashMap<>();
        UUID uuid1 = UUIDUtil.generateUUID();
        UUID uuid2 = new UUID(uuid1.getUuid().toString());

        map.put(uuid1, 1);

        Assertions.assertNotNull(map.get(uuid2));
    }

    @Test
    public void UUIDTest() {
        TicketController ticketController = new TicketController();
        TicketDomainEqual domain = new TicketDomainEqual() {
            @Override
            public boolean equals(Object obj) {
                return true;
            }
        };
        ticketController.register(domain, 0L);

        Ticket ticket = ticketController.issueTicket(domain);
        Assertions.assertFalse(ticket.canEnter());
        Ticket updateTicket = ticketController.updateTicket(domain, ticket.getUuid().toString());
    }

}
