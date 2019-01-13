package org.iatoki.judgels.jerahmeel.gabriel;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import judgels.gabriel.api.GabrielClientConfiguration;

import javax.inject.Named;

public final class GabrielModule extends AbstractModule {
    @Override
    protected void configure() {}

    @Provides
    GabrielClientConfiguration config() {
        Config config = ConfigFactory.load();
        return new GabrielClientConfiguration.Builder()
                .clientJid(config.getString("gabriel.clientJid"))
                .build();
    }

    @Provides
    @Named("gabrielClientJid")
    String gabrielClientJid(GabrielClientConfiguration config) {
        return config.getClientJid();
    }
}
