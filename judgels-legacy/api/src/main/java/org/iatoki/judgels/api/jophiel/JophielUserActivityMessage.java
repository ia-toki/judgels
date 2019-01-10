package org.iatoki.judgels.api.jophiel;

public final class JophielUserActivityMessage {

    private long time;
    private String userJid;
    private String log;
    private String ipAddress;

    public JophielUserActivityMessage(long time, String userJid, String log, String ipAddress) {
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
