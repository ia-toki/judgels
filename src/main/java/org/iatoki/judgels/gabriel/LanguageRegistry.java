package org.iatoki.judgels.gabriel;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

public final class LanguageRegistry {
    private static LanguageRegistry INSTANCE;

    private final Map<String, Language> registry;

    private LanguageRegistry() {
        this.registry = Maps.newHashMap();

        populateLanguages();
    }

    public Language getLanguage(String gradingLanguage) {
        if (!registry.containsKey(gradingLanguage)) {
            throw new IllegalArgumentException("Grading language " + gradingLanguage + " not found");
        }
        return registry.get(gradingLanguage);
    }

    public Map<String, String> getGradingLanguages() {
        return registry.keySet().stream().collect(Collectors.toMap(e -> e, e -> registry.get(e).getName()));
    }

    public static LanguageRegistry getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new LanguageRegistry();
        }
        return INSTANCE;
    }

    private void populateLanguages() {
        for (Language language : ServiceLoader.load(Language.class)) {
            registry.put(getLanguageSimpleName(language), language);
        }
    }

    private String getLanguageSimpleName(Language language) {
        String name = language.getClass().getSimpleName();
        return name.substring(0, name.length() - "Language".length());
    }
}
