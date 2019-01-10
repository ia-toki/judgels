package org.iatoki.judgels.sandalphon.client.problem;

public final class ClientProblem {

    private final long id;
    private final String clientJid;
    private final String clientName;
    private final String problemJid;
    private final String secret;

    public ClientProblem(long id, String clientJid, String clientName, String problemJid, String secret) {
        this.id = id;
        this.clientJid = clientJid;
        this.clientName = clientName;
        this.problemJid = problemJid;
        this.secret = secret;
    }

    public long getId() {
        return id;
    }

    public String getClientJid() {
        return clientJid;
    }

    public String getClientName() {
        return clientName;
    }

    public String getProblemJid() {
        return problemJid;
    }

    public String getSecret() {
        return secret;
    }
}
