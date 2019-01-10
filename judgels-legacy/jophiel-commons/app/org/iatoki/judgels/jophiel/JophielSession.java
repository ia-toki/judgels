package org.iatoki.judgels.jophiel;

public class JophielSession {
    private final String userJid;
    private final String token;

    public JophielSession(String userJid, String token) {
        this.userJid = userJid;
        this.token = token;
    }

    public String getUserJid() {
        return userJid;
    }

    public String getToken() {
        return token;
    }
}
