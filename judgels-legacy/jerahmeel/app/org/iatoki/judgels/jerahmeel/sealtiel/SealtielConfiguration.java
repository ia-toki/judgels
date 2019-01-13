package org.iatoki.judgels.jerahmeel.sealtiel;

public class SealtielConfiguration {
    private final String baseUrl;
    private final String clientJid;
    private final String clientSecret;
    private final String gabrielClientJid;

    public SealtielConfiguration(String baseUrl, String clientJid, String clientSecret, String gabrielClientJid) {
        this.baseUrl = baseUrl;
        this.clientJid = clientJid;
        this.clientSecret = clientSecret;
        this.gabrielClientJid = gabrielClientJid;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getClientJid() {
        return clientJid;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getGabrielClientJid() {
        return gabrielClientJid;
    }
}
