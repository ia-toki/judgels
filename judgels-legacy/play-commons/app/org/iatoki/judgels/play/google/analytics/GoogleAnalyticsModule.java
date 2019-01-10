package org.iatoki.judgels.play.google.analytics;

import com.google.inject.AbstractModule;

public final class GoogleAnalyticsModule extends AbstractModule {

    @Override
    protected void configure() {
        bindConstant().annotatedWith(GoogleAnalyticsConfigSource.class).to(true);
    }
}
