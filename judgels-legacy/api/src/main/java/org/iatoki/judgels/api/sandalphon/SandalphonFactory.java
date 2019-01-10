package org.iatoki.judgels.api.sandalphon;

import org.iatoki.judgels.api.sandalphon.impls.SandalphonImpl;

public final class SandalphonFactory {

    private SandalphonFactory() {
        // prevent instantiation
    }

    public static Sandalphon createSandalphon(String baseUrl) {
        return new SandalphonImpl(baseUrl);
    }
}
