package org.iatoki.judgels.play.google.serviceaccount;

import com.google.inject.AbstractModule;
import org.iatoki.judgels.Config;
import org.iatoki.judgels.play.ApplicationConfig;

public final class GoogleServiceAccountModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Config.class).annotatedWith(GoogleServiceAccountConfigSource.class).toInstance(ApplicationConfig.getInstance());
    }
}
