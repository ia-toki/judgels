package org.iatoki.judgels.play.seo;

import org.iatoki.judgels.Config;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class SeoConfig {

    private final String metaKeywords;
    private final String metaDescription;

    @Inject
    public SeoConfig(@SeoConfigSource Config config) {
        this.metaKeywords = config.requireString("seo.metaKeywords");
        this.metaDescription = config.requireString("seo.metaDescription");
    }

    public String getMetaKeywords() {
        return metaKeywords;
    }

    public String getMetaDescription() {
        return metaDescription;
    }
}
