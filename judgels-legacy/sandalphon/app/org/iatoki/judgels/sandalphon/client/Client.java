package org.iatoki.judgels.sandalphon.client;

import org.iatoki.judgels.play.api.JudgelsAppClient;

public final class Client implements JudgelsAppClient {

    private final long id;
    private final String jid;
    private final String name;
    private final String secret;

    public Client(long id, String jid, String name, String secret) {
        this.id = id;
        this.jid = jid;
        this.name = name;
        this.secret = secret;
    }

    public long getId() {
        return id;
    }

    @Override
    public String getJid() {
        return jid;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getSecret() {
        return secret;
    }
}
