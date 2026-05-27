package judgels.persistence;

import java.util.Optional;

public class TestActorProvider implements ActorProvider {
    public static final String ACTOR_JID = "actorJid";
    public static final String ACTOR_IP = "actorIp";

    private Optional<String> jid;
    private Optional<String> ipAddress;

    public TestActorProvider() {
        this(ACTOR_JID, ACTOR_IP);
    }

    public TestActorProvider(String jid, String ipAddress) {
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

    public void setJid(String jid) {
        this.jid = Optional.of(jid);
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = Optional.of(ipAddress);
    }
}
