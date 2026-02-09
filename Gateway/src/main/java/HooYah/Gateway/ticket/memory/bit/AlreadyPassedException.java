package HooYah.Gateway.ticket.memory.bit;

public class AlreadyPassedException extends IllegalStateException {

    public AlreadyPassedException(int id) {
        super("Ticket "+id+" has already been passed");
    }

}
