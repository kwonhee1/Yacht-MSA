package HooYah.Gateway.domain.vo;

public enum Protocol {
    http,
    https;

    public static Protocol getProtocol(String protocolStr) {
        for(Protocol p : Protocol.values())
            if(p.name().equals(protocolStr))
                return p;

        return Protocol.http;
    }
}
