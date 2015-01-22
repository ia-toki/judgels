package org.iatoki.judgels.gabriel;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.text.WordUtils;
import org.iatoki.judgels.gabriel.languages.CppLanguage;

import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Collectors;

public final class LanguageRegistry {

    private static LanguageRegistry INSTANCE;

    private final EnumMap<GradingLanguage, Language> registry;

    private LanguageRegistry() {
        this.registry = Maps.newEnumMap(GradingLanguage.class);

        populateLanguages();
    }

    public Language getLanguage(GradingLanguage gradingLanguage) {
        if (!registry.containsKey(gradingLanguage)) {
            throw new IllegalArgumentException("Grading language " + gradingLanguage + " not found");
        }
        return registry.get(gradingLanguage);
    }

    public Map<String, String> getGradingLanguages() {
        return registry.keySet().stream().collect(Collectors.toMap(e -> e.toString(), e -> e.getName()));
    }

    public static LanguageRegistry getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new LanguageRegistry();
        }
        return INSTANCE;
    }

    private void populateLanguages() {
        registry.put(GradingLanguage.CPP, new CppLanguage("/usr/bin/g++"));
    }
}
