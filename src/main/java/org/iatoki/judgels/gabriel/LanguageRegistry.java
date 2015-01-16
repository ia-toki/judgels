package org.iatoki.judgels.gabriel;

import com.google.common.collect.Maps;
import org.iatoki.judgels.gabriel.languages.CppLanguage;

import java.util.Map;
import java.util.stream.Collectors;

public final class LanguageRegistry {

    private static LanguageRegistry INSTANCE;

    private final Map<String, Language> registry;

    private LanguageRegistry() {
        this.registry = Maps.newHashMap();

        populateLanguages();
    }

    public Language getLanguage(String languageName) {
        if (!registry.containsKey(languageName)) {
            throw new IllegalArgumentException("Language " + languageName + " not found");
        }
        return registry.get(languageName);
    }

    public Map<String, String> getLanguages() {
        return registry.keySet().stream().collect(Collectors.toMap(e -> e, e -> registry.get(e).getName()));
    }

    public static LanguageRegistry getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new LanguageRegistry();
        }
        return INSTANCE;
    }

    private void populateLanguages() {
        registry.put("CppLanguage", new CppLanguage("/usr/bin/g++"));
    }
}
