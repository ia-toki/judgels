package org.iatoki.judgels;

public final class AWSFileURL {

    private final String url;
    private final long expireTime;

    public AWSFileURL(String url, long expireTime) {
        this.url = url;
        this.expireTime = expireTime;
    }

    public String getUrl() {
        return url;
    }

    public long getExpireTime() {
        return expireTime;
    }
}
