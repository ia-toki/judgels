package org.iatoki.judgels.api.sandalphon.impls;

import org.iatoki.judgels.api.sandalphon.Sandalphon;
import org.iatoki.judgels.api.sandalphon.SandalphonClientAPI;

public final class SandalphonImpl implements Sandalphon {

    private final String baseUrl;

    public SandalphonImpl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public SandalphonClientAPI connectToClientAPI(String clientJid, String clientSecret) {
        return new SandalphonClientAPIImpl(baseUrl, clientJid, clientSecret);
    }
}
