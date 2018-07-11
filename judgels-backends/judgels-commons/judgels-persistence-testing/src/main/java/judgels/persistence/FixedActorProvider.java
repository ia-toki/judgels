package judgels.persistence;

import java.util.Optional;

public class FixedActorProvider implements ActorProvider {
    private final Optional<String> jid;
    private final Optional<String> ipAddress;

    public FixedActorProvider() {
        this.jid = Optional.empty();
        this.ipAddress = Optional.empty();
    }

    public FixedActorProvider(String jid, String ipAddress) {
        this.jid = Optional.of(jid);
        this.ipAddress = Optional.of(ipAddress);
    }

    @Override
    public Optional<String> getJid() {
        return jid;
    }

    @Override
    public Optional<String> getIpAddress() {
        return ipAddress;
    }

}
