package HooYah.Gateway.ticket.uuid.vo;

import java.util.Objects;

public class UUID {

    private final String uuid;

    public UUID(String uuid) {
        this.uuid = uuid;
    }
    public String getUuid() {
        return uuid;
    }

    @Override
    public String toString() {
        return uuid;
    }

    @Override
    public boolean equals(Object other) {
        if(other.getClass() != UUID.class)
            return false;

        if(this.uuid.equals(((UUID) other).uuid))
            return true;

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
