package org.iatoki.judgels.play.banner;

import org.iatoki.judgels.Config;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class BannerConfig {

    private final String link;
    private final String imageSrc;

    @Inject
    public BannerConfig(@BannerConfigSource Config config) {
        this.link = config.requireString("banner.link");
        this.imageSrc = config.requireString("banner.imageSrc");
    }

    public String getLink() {
        return link;
    }

    public String getImageSrc() {
        return imageSrc;
    }
}
