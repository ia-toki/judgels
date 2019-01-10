package org.iatoki.judgels.api.impls;

import org.apache.http.client.methods.HttpRequestBase;
import org.iatoki.judgels.api.JudgelsPublicAPI;

public abstract class AbstractJudgelsPublicAPIImpl extends AbstractJudgelsAPIImpl implements JudgelsPublicAPI {

    private String accessToken;

    public AbstractJudgelsPublicAPIImpl(String baseUrl) {
        super(baseUrl);
        this.accessToken = null;
    }

    public AbstractJudgelsPublicAPIImpl(String baseUrl, String apiUrlPrefix) {
        super(baseUrl, apiUrlPrefix);
        this.accessToken = null;
    }

    @Override
    public void useOnBehalfOfUser(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public void useAnonymously() {
        this.accessToken = null;
    }

    protected final void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    protected final void setAuthorization(HttpRequestBase httpRequest) {
        if (accessToken != null) {
            httpRequest.setHeader("Authorization", "Bearer " + accessToken);
        } else {
            httpRequest.removeHeaders("Authorization");
        }
    }
}
