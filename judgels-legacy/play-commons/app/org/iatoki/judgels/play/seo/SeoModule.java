package org.iatoki.judgels.play.seo;

import com.google.inject.AbstractModule;
import org.iatoki.judgels.Config;
import org.iatoki.judgels.play.ApplicationConfig;

public final class SeoModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Config.class).annotatedWith(SeoConfigSource.class).toInstance(ApplicationConfig.getInstance());
    }
}
