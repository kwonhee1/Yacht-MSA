package HooYah.Gateway.ticket.memory;

import HooYah.Gateway.ticket.uuid.vo.UUID;

public class NoValueException extends RuntimeException {
    public NoValueException(UUID uuid) {
        super(uuid.getUuid() + "는 없는 값입니다");
    }
}
