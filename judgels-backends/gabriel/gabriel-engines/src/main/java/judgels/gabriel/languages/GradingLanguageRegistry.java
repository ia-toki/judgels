package judgels.gabriel.languages;

import com.google.common.collect.ImmutableList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import judgels.gabriel.api.GradingLanguage;
import judgels.gabriel.languages.c.CGradingLanguage;
import judgels.gabriel.languages.cpp.Cpp11GradingLanguage;
import judgels.gabriel.languages.cpp.CppGradingLanguage;
import judgels.gabriel.languages.java.JavaGradingLanguage;
import judgels.gabriel.languages.pascal.PascalGradingLanguage;
import judgels.gabriel.languages.python.Python3GradingLanguage;

public class GradingLanguageRegistry {
    private static final GradingLanguageRegistry INSTANCE = new GradingLanguageRegistry();

    private static final List<GradingLanguage> LANGUAGES = ImmutableList.of(
            new CGradingLanguage(),
            new CppGradingLanguage(),
            new Cpp11GradingLanguage(),
            new JavaGradingLanguage(),
            new PascalGradingLanguage(),
            new Python3GradingLanguage(),
            new OutputOnlyGradingLanguage());

    private static final Map<String, GradingLanguage> LANGUAGES_BY_SIMPLE_NAME = LANGUAGES.stream().collect(
            LinkedHashMap::new,
            (map, language) -> map.put(getSimpleName(language), language),
            Map::putAll);

    private static final Map<String, String> LANGUAGE_NAMES_BY_SIMPLE_NAME = LANGUAGES.stream().collect(
            LinkedHashMap::new,
            (map, language) -> map.put(getSimpleName(language), language.getName()),
            Map::putAll);

    private GradingLanguageRegistry() {}

    public static GradingLanguageRegistry getInstance() {
        return INSTANCE;
    }

    public GradingLanguage get(String simpleName) {
        GradingLanguage language = LANGUAGES_BY_SIMPLE_NAME.get(simpleName);
        if (language == null) {
            throw new IllegalArgumentException("Grading language " + simpleName + " not found");
        }
        return language;
    }

    public Map<String, String> getNamesMap() {
        return LANGUAGE_NAMES_BY_SIMPLE_NAME;
    }

    private static String getSimpleName(GradingLanguage language) {
        String name = language.getClass().getSimpleName();
        return name.substring(0, name.length() - "GradingLanguage".length());
    }
}
