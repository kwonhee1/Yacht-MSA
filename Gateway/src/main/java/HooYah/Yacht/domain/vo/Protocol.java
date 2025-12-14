package HooYah.Yacht.domain.vo;

public enum Protocol {
    Http,
    Https;

    public static Protocol getProtocol(String protocolStr) {
        for(Protocol p : Protocol.values())
            if(p.name().equals(protocolStr))
                return p;

        return Protocol.Http;
    }
}
