package org.iatoki.judgels.jophiel.api;

import org.iatoki.judgels.play.api.JudgelsAPIIdentity;

public final class JophielUserAPIIdentity implements JudgelsAPIIdentity {

    private final String userJid;
    private final String username;

    public JophielUserAPIIdentity(String userJid, String username) {
        this.userJid = userJid;
        this.username = username;
    }

    public String getUserJid() {
        return userJid;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String getName() {
        return username;
    }
}
