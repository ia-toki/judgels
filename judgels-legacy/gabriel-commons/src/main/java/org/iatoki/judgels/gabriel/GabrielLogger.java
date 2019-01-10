package org.iatoki.judgels.gabriel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class GabrielLogger {
    private static Logger LOGGER;

    private GabrielLogger() {
        // prevent instantiation
    }

    public static Logger getLogger() {
        if (LOGGER == null) {
            LOGGER = LoggerFactory.getLogger("application");
        }
        return LOGGER;
    }
}
