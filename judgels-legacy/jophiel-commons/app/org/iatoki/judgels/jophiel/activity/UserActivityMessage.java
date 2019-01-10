package org.iatoki.judgels.jophiel.activity;

public final class UserActivityMessage {

    private final long time;
    private final String userJid;
    private final String log;
    private final String ipAddress;

    public UserActivityMessage(long time, String userJid, String log, String ipAddress) {
        this.time = time;
        this.userJid = userJid;
        this.log = log;
        this.ipAddress = ipAddress;
    }

    public long getTime() {
        return time;
    }

    public String getUserJid() {
        return userJid;
    }

    public String getLog() {
        return log;
    }

    public String getIpAddress() {
        return ipAddress;
    }
}
