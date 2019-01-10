package org.iatoki.judgels.api.jophiel;

public final class JophielUser {

    private String jid;
    private String username;
    private String avatarUrl;

    public JophielUser(String jid, String username, String avatarUrl) {
        this.jid = jid;
        this.username = username;
        this.avatarUrl = avatarUrl;
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

    public String getProfilePictureUrl() {
        return avatarUrl;
    }
}
