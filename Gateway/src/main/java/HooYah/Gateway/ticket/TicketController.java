package HooYah.Gateway.ticket;

import HooYah.Gateway.ticket.dto.Ticket;
import HooYah.Gateway.ticket.service.TicketDomainEqual;
import HooYah.Gateway.ticket.service.TicketService;
import HooYah.Gateway.ticket.uuid.vo.UUID;
import java.util.HashMap;
import java.util.Map;

public class TicketController {

    private Map<TicketDomainEqual, TicketService> serviceMap = new HashMap<TicketDomainEqual, TicketService>();

    public void register(TicketDomainEqual domain, Long maxConcurrentCount) {
        if(serviceMap.containsKey(domain)) {
            throw new IllegalStateException("Already registered");
        }

        serviceMap.put(domain, new TicketService(maxConcurrentCount));
    }

    public Ticket issueTicket(TicketDomainEqual domain) {
        return serviceMap.get(domain).issueTicket();
    }

    public Ticket updateTicket(TicketDomainEqual domain, String uuid) {
        return serviceMap.get(domain).updateTicket(new UUID(uuid));
    }

    public void release(TicketDomainEqual domain, Ticket ticket) {
        serviceMap.get(domain).release(ticket);
    }

}
