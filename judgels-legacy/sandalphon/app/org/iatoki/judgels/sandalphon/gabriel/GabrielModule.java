package org.iatoki.judgels.sandalphon.gabriel;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import judgels.gabriel.api.GabrielClientConfiguration;
import judgels.sandalphon.SandalphonConfiguration;

import javax.inject.Named;

public final class GabrielModule extends AbstractModule {
    @Override
    protected void configure() {}

    @Provides
    GabrielClientConfiguration config(SandalphonConfiguration config) {
        return config.getGabrielConfig();
    }

    @Provides
    @Named("gabrielClientJid")
    String gabrielClientJid(GabrielClientConfiguration config) {
        return config.getClientJid();
    }
}
