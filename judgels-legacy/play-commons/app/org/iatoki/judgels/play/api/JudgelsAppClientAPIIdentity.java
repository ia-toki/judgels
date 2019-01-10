package org.iatoki.judgels.play.api;

public final class JudgelsAppClientAPIIdentity implements JudgelsAPIIdentity {

    private final String clientJid;
    private final String clientName;

    public JudgelsAppClientAPIIdentity(String clientJid, String clientName) {
        this.clientJid = clientJid;
        this.clientName = clientName;
    }

    public String getClientJid() {
        return clientJid;
    }

    public String getClientName() {
        return clientName;
    }

    @Override
    public String getName() {
        return clientName;
    }
}
