package org.iatoki.judgels.play.sponsor;

import com.google.inject.AbstractModule;
import org.iatoki.judgels.Config;
import org.iatoki.judgels.play.ApplicationConfig;

public final class SponsorModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Config.class).annotatedWith(SponsorConfigSource.class).toInstance(ApplicationConfig.getInstance());
    }
}
