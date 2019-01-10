package org.iatoki.judgels.play.banner;

import com.google.inject.AbstractModule;
import org.iatoki.judgels.Config;
import org.iatoki.judgels.play.ApplicationConfig;

public final class BannerModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Config.class).annotatedWith(BannerConfigSource.class).toInstance(ApplicationConfig.getInstance());
    }
}
