package org.iatoki.judgels.api.jophiel.impls;

import org.iatoki.judgels.api.jophiel.Jophiel;
import org.iatoki.judgels.api.jophiel.JophielClientAPI;
import org.iatoki.judgels.api.jophiel.JophielPublicAPI;

public final class JophielImpl implements Jophiel {

    private final String baseUrl;

    public JophielImpl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public JophielClientAPI connectToClientAPI(String clientJid, String clientSecret) {
        return new JophielClientAPIImpl(baseUrl, clientJid, clientSecret);
    }

    @Override
    public JophielPublicAPI connectToPublicAPI() {
        return new JophielPublicAPIImpl(baseUrl);
    }
}
