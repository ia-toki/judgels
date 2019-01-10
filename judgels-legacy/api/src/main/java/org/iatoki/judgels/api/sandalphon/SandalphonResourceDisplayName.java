package org.iatoki.judgels.api.sandalphon;

import java.util.Map;

public final class SandalphonResourceDisplayName {

    private final String slug;
    private final String defaultLanguage;
    private final Map<String, String> titlesByLanguage;

    public SandalphonResourceDisplayName(String slug, String defaultLanguage, Map<String, String> titlesByLanguage) {
        this.slug = slug;
        this.defaultLanguage = defaultLanguage;
        this.titlesByLanguage = titlesByLanguage;
    }

    public String getSlug() {
        return slug;
    }

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public Map<String, String> getTitlesByLanguage() {
        return titlesByLanguage;
    }
}
