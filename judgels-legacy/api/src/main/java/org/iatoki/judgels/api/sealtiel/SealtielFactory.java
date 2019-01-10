package org.iatoki.judgels.api.sealtiel;

import org.iatoki.judgels.api.sealtiel.impls.SealtielImpl;

public final class SealtielFactory {

    private SealtielFactory() {
        // prevent instantiation
    }

    public static Sealtiel createSealtiel(String baseUrl) {
        return new SealtielImpl(baseUrl);
    }
}
