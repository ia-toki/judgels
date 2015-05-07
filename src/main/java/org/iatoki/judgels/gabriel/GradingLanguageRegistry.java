package org.iatoki.judgels.gabriel;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

public final class GradingLanguageRegistry {
    private static GradingLanguageRegistry INSTANCE;

    private final Map<String, GradingLanguage> registry;

    private GradingLanguageRegistry() {
        this.registry = Maps.newLinkedHashMap();

        populateLanguages();
    }

    public GradingLanguage getLanguage(String language) {
        if (!registry.containsKey(language)) {
            throw new IllegalArgumentException("Grading language " + language + " not found");
        }
        return registry.get(language);
    }

    public Map<String, String> getGradingLanguages() {
        return ImmutableMap.copyOf(Maps.transformValues(registry, language -> language.getName()));
    }

    public static GradingLanguageRegistry getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GradingLanguageRegistry();
        }
        return INSTANCE;
    }

    private void populateLanguages() {
        List<GradingLanguage> languages = Lists.newArrayList(ServiceLoader.load(GradingLanguage.class));

        for (GradingLanguage language : languages) {
            registry.put(getLanguageSimpleName(language), language);
        }
    }

    private String getLanguageSimpleName(GradingLanguage language) {
        String name = language.getClass().getSimpleName();
        return name.substring(0, name.length() - "GradingLanguage".length());
    }
}
