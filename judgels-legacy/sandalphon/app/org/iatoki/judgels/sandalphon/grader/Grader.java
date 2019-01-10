package org.iatoki.judgels.sandalphon.grader;

import org.iatoki.judgels.play.api.JudgelsAppClient;

public final class Grader implements JudgelsAppClient {

    private final long id;
    private final String jid;
    private final String name;
    private final String secret;

    public Grader(long id, String jid, String name, String secret) {
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
