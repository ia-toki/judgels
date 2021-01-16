package org.iatoki.judgels.sandalphon;

public final class SandalphonControllerUtils {

    private static SandalphonControllerUtils INSTANCE;

    public boolean isAdmin() {
        return SandalphonUtils.hasRole("ADMIN");
    }

    public boolean isWriter() {
        return !SandalphonUtils.hasRole("COACH");
    }

    public static synchronized void buildInstance() {
        if (INSTANCE != null) {
            throw new UnsupportedOperationException("SandalphonControllerUtils instance has already been built");
        }
        INSTANCE = new SandalphonControllerUtils();
    }

    public static SandalphonControllerUtils getInstance() {
        if (INSTANCE == null) {
            throw new UnsupportedOperationException("SandalphonControllerUtils instance has not been built");
        }
        return INSTANCE;
    }
}
