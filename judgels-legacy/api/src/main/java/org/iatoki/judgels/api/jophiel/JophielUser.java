package org.iatoki.judgels.api.jophiel;

public final class JophielUser {

    private String jid;
    private String username;

    public JophielUser(String jid, String username) {
        this.jid = jid;
        this.username = username;
    }

    public String getJid() {
        return jid;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return username;
    }
}
