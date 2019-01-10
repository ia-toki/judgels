package org.iatoki.judgels.api.jophiel;

import org.iatoki.judgels.api.jophiel.impls.JophielImpl;

public final class JophielFactory {

    private JophielFactory() {
        // prevent instantiation
    }

    public static Jophiel createJophiel(String baseUrl) {
        return new JophielImpl(baseUrl);
    }
}
