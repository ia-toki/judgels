package org.iatoki.judgels.api.sealtiel.impls;

import org.iatoki.judgels.api.sealtiel.Sealtiel;
import org.iatoki.judgels.api.sealtiel.SealtielClientAPI;

public class SealtielImpl implements Sealtiel {

    private final String baseUrl;

    public SealtielImpl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public SealtielClientAPI connectToClientAPI(String clientJid, String clientSecret) {
        return new SealtielClientAPIImpl(baseUrl, clientJid, clientSecret);
    }
}
