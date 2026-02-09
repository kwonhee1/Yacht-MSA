package HooYah.Gateway.ticket.dto;

import HooYah.Gateway.ticket.uuid.vo.UUID;

public class Ticket {

    private final static Integer ENTER_NUMBER = 0;

    private final Integer waitingNumber;
    private final UUID uuid;

    public Ticket(UUID uuid, Integer waitingNumber) {
        this.uuid = uuid;
        this.waitingNumber = waitingNumber;
    }

    public static Ticket enterTicket() {
        return new Ticket(null, ENTER_NUMBER);
    }

    public Integer getWaitingNumber() {
        return waitingNumber;
    }

    public UUID getUuid() {
        return uuid;
    }

    public boolean canEnter() {
        return waitingNumber.equals(ENTER_NUMBER);
    }

}
