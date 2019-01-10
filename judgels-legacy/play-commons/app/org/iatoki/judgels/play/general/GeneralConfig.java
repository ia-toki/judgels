package org.iatoki.judgels.play.general;

import org.iatoki.judgels.Config;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class GeneralConfig {

    private final String name;
    private final String version;
    private final String title;
    private final String copyright;
    private final String canonicalUrl;
    private final String githubUrl;

    @Inject
    public GeneralConfig(@GeneralConfigSource Config config, @GeneralName String name, @GeneralVersion String version) {
        this.name = name;
        this.version = version;
        this.title = config.requireString("general.title");
        this.copyright = config.requireString("general.copyright");
        this.canonicalUrl = config.requireString("general.canonicalUrl");
        this.githubUrl = config.requireString("general.githubUrl");
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getTitle() {
        return title;
    }

    public String getCopyright() {
        return copyright;
    }

    public String getCanonicalUrl() {
        return canonicalUrl;
    }

    public String getGithubUrl() {
        return githubUrl;
    }
}
