package org.iatoki.judgels.play.google.serviceaccount;

import org.iatoki.judgels.Config;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class GoogleServiceAccountConfig {

    private String clientId;
    private String clientEmail;
    private String privateKeyId;
    private String privateKey;

    @Inject
    public GoogleServiceAccountConfig(@SuppressWarnings("unused") @GoogleServiceAccountConfigSource boolean enabled, Config config) {
        this.clientId = config.requireString("google.serviceAccount.clientId");
        this.clientEmail = config.requireString("google.serviceAccount.clientEmail");
        this.privateKeyId = config.requireString("google.serviceAccount.privateKeyId");
        this.privateKey = config.requireString("google.serviceAccount.privateKey");
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public String getPrivateKeyId() {
        return privateKeyId;
    }

    public String getPrivateKey() {
        return privateKey;
    }
}
