package org.iatoki.judgels.api.impls;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.methods.HttpRequestBase;
import org.iatoki.judgels.api.JudgelsClientAPI;

public abstract class AbstractJudgelsClientAPIImpl extends AbstractJudgelsAPIImpl implements JudgelsClientAPI {

    private final String clientJid;
    private final String clientSecret;

    protected AbstractJudgelsClientAPIImpl(String baseUrl, String clientJid, String clientSecret) {
        super(baseUrl);
        this.clientJid = clientJid;
        this.clientSecret = clientSecret;
    }

    protected AbstractJudgelsClientAPIImpl(String baseUrl, String apiUrlPrefix, String clientJid, String clientSecret) {
        super(baseUrl, apiUrlPrefix);
        this.clientJid = clientJid;
        this.clientSecret = clientSecret;
    }

    @Override
    protected final void setAuthorization(HttpRequestBase httpRequest) {
        String credentials = clientJid + ":" + clientSecret;
        httpRequest.setHeader("Authorization", "Basic " + Base64.encodeBase64String(credentials.getBytes()));
    }

    @Override
    public String getClientJid() {
        return clientJid;
    }
}
