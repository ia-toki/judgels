package org.iatoki.judgels.api.impls;

import org.apache.http.client.methods.HttpRequestBase;

public abstract class AbstractJudgelsPublicAPIImpl extends AbstractJudgelsAPIImpl {

    public AbstractJudgelsPublicAPIImpl(String baseUrl, String apiUrlPrefix) {
        super(baseUrl, apiUrlPrefix);
    }

    @Override
    protected final void setAuthorization(HttpRequestBase httpRequest) {}
}
