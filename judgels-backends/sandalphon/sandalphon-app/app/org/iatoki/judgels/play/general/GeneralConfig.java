package org.iatoki.judgels.play.general;

import com.typesafe.config.Config;
import javax.inject.Singleton;

@Singleton
public final class GeneralConfig {

    private final String name;
    private final String version;
    private final String title;
    private final String copyright;
    private final String canonicalUrl;
    private final String githubUrl;

    public GeneralConfig(Config config, String name, String version) {
        this.name = name;
        this.version = version;
        this.title = config.getString("general.title");
        this.copyright = config.getString("general.copyright");
        this.canonicalUrl = config.getString("general.canonicalUrl");
        this.githubUrl = config.getString("general.githubUrl");
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
