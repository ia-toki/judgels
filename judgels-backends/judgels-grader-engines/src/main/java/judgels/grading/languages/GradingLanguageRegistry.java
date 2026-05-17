package judgels.grading.languages;

import com.google.common.collect.ImmutableList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import judgels.grading.api.GradingLanguage;
import judgels.grading.languages.c.CGradingLanguage;
import judgels.grading.languages.cpp.Cpp11GradingLanguage;
import judgels.grading.languages.cpp.Cpp17GradingLanguage;
import judgels.grading.languages.cpp.Cpp20GradingLanguage;
import judgels.grading.languages.cpp.CppGradingLanguage;
import judgels.grading.languages.go.GoGradingLanguage;
import judgels.grading.languages.java.JavaGradingLanguage;
import judgels.grading.languages.pascal.PascalGradingLanguage;
import judgels.grading.languages.python.PyPy3GradingLanguage;
import judgels.grading.languages.python.Python3GradingLanguage;
import judgels.grading.languages.rust.Rust2021GradingLanguage;

public class GradingLanguageRegistry {
    private static final GradingLanguageRegistry INSTANCE = new GradingLanguageRegistry();

    private static final List<GradingLanguage> LANGUAGES = ImmutableList.of(
            new CGradingLanguage(),
            new CppGradingLanguage(),
            new Cpp11GradingLanguage(),
            new Cpp17GradingLanguage(),
            new Cpp20GradingLanguage(),
            new GoGradingLanguage(),
            new JavaGradingLanguage(),
            new PascalGradingLanguage(),
            new PyPy3GradingLanguage(),
            new Python3GradingLanguage(),
            new Rust2021GradingLanguage(),
            new OutputOnlyGradingLanguage());

    private static final List<GradingLanguage> VISIBLE_LANGUAGES = LANGUAGES.stream()
            .filter(GradingLanguage::isVisible)
            .collect(Collectors.toList());

    private static final Map<String, GradingLanguage> LANGUAGES_BY_SIMPLE_NAME = LANGUAGES.stream().collect(
            LinkedHashMap::new,
            (map, language) -> map.put(getSimpleName(language), language),
            Map::putAll);

    private static final Map<String, String> LANGUAGE_NAMES_BY_SIMPLE_NAME = LANGUAGES.stream().collect(
            LinkedHashMap::new,
            (map, language) -> map.put(getSimpleName(language), language.getName()),
            Map::putAll);

    private static final Map<String, String> VISIBLE_LANGUAGE_NAMES_BY_SIMPLE_NAME = VISIBLE_LANGUAGES.stream().collect(
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

    public Map<String, String> getLanguages() {
        return LANGUAGE_NAMES_BY_SIMPLE_NAME;
    }

    public Map<String, String> getVisibleLanguages() {
        return VISIBLE_LANGUAGE_NAMES_BY_SIMPLE_NAME;
    }

    private static String getSimpleName(GradingLanguage language) {
        String name = language.getClass().getSimpleName();
        return name.substring(0, name.length() - "GradingLanguage".length());
    }
}
