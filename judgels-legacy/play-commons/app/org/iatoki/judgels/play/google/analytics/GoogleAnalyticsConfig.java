package org.iatoki.judgels.play.google.analytics;

import org.iatoki.judgels.Config;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class GoogleAnalyticsConfig {

    private final String id;
    private final String viewId;

    @Inject
    public GoogleAnalyticsConfig(@SuppressWarnings("unused") @GoogleAnalyticsConfigSource boolean enabled, Config config) {
        this.id = config.requireString("google.analytics.id");
        this.viewId = config.requireString("google.analytics.viewId");
    }

    public String getId() {
        return id;
    }

    public String getViewId() {
        return viewId;
    }
}
