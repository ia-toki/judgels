package org.iatoki.judgels.api.sandalphon;

import com.google.gson.Gson;

import java.util.Map;

public final class SandalphonLesson {

    private final String jid;
    private final String slug;
    private final String defaultLanguage;
    private final Map<String, String> titlesByLanguage;

    public SandalphonLesson(String jid, String slug, String defaultLanguage, Map<String, String> titlesByLanguage) {
        this.jid = jid;
        this.slug = slug;
        this.defaultLanguage = defaultLanguage;
        this.titlesByLanguage = titlesByLanguage;
    }

    public String getJid() {
        return jid;
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

    public String getDisplayName() {
        return new Gson().toJson(new SandalphonResourceDisplayName(slug, defaultLanguage, titlesByLanguage));
    }
}
