package HooYah.Gateway.ticket.uuid.util;

import HooYah.Gateway.ticket.uuid.vo.UUID;

public class UUIDUtil {

    public static UUID generateUUID() {
        return new UUID(java.util.UUID.randomUUID().toString());
        // return new UUID(java.util.UUID.randomUUID().toString()).replace("-", "");
    }
}
