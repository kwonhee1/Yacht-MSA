package HooYah.Gateway.ticket.service;

import HooYah.Gateway.ticket.memory.bit.AlreadyPassedException;
import HooYah.Gateway.ticket.memory.bit.BitMemory;
import HooYah.Gateway.ticket.dto.Ticket;
import HooYah.Gateway.ticket.uuid.util.UUIDUtil;
import HooYah.Gateway.ticket.uuid.vo.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class TicketService {

    private final Long maxConcurrentCount;
    private final AtomicInteger concurrentCount;

    private final BitMemory memory = new BitMemory(Integer.MAX_VALUE);

    public TicketService(Long maxCount) {
        this.maxConcurrentCount = maxCount;
        this.concurrentCount = new AtomicInteger(0);
    }

    public Ticket issueTicket() {
        UUID uuid = UUIDUtil.generateUUID();
        int waitingNumber = memory.put(uuid);

        Ticket newTicket = new Ticket(uuid, waitingNumber);

        if(canEnter(newTicket))
            return Ticket.enterTicket();
        else
            return newTicket;
    }

    public Ticket updateTicket(UUID uuid) {
        Ticket newTicket;
        try { // try update Ticket
            int waitingNumber ;
            // waitingNumber = memory.refreshValue(uuid);
            UUID newUUID = UUIDUtil.generateUUID();
            waitingNumber = memory.updateKey(uuid, newUUID);
            newTicket = new Ticket(newUUID, waitingNumber);
        } catch (AlreadyPassedException e) {
            e.printStackTrace();
            return issueTicket();
        }

        if(canEnter(newTicket)) // 여기서 이미 uuid 값을 pop했음 -> 더 이상 uuid 를 쓸 수 없어야함
            return Ticket.enterTicket();
        else {
            return newTicket;
        }
    }

    private boolean canEnter(Ticket ticket) {
        int waitingNumber = ticket.getWaitingNumber();
        int nowConcurrentCount;
        do {
            nowConcurrentCount = concurrentCount.get();
            if(waitingNumber > maxConcurrentCount - nowConcurrentCount) {
                return false;
            }
        } while(!concurrentCount.compareAndSet(nowConcurrentCount, nowConcurrentCount+1));

        memory.pop(ticket.getUuid());
        return true;
    }

    public void release(Ticket ticket) {
        if(!ticket.canEnter())
            throw new IllegalStateException("Not a Enter Ticket");

        concurrentCount.decrementAndGet();
    }


}
