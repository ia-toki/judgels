package org.iatoki.judgels.play;

import com.typesafe.config.ConfigFactory;
import org.iatoki.judgels.Config;

public final class ApplicationConfig {
    private static final Config INSTANCE = new Config(ConfigFactory.load());

    private ApplicationConfig() {
        // prevent instantiation
    }

    public static Config getInstance() {
        return INSTANCE;
    }
}
