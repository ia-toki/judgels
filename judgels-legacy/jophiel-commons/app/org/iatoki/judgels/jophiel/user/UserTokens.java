package org.iatoki.judgels.jophiel.user;

public final class UserTokens {

    private String userJid;
    private String accessToken;
    private String refreshToken;
    private String idToken;
    private long expireTime;

    public UserTokens(String userJid, String accessToken, String refreshToken, String idToken, long expireTime) {
        this.userJid = userJid;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.idToken = idToken;
        this.expireTime = expireTime;
    }

    public String getUserJid() {
        return userJid;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getIdToken() {
        return idToken;
    }

    public long getExpireTime() {
        return expireTime;
    }
}
