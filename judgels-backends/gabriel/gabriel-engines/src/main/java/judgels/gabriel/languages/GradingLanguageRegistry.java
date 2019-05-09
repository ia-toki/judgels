package judgels.gabriel.languages;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import judgels.gabriel.api.GradingLanguage;
import judgels.gabriel.languages.c.CGradingLanguage;
import judgels.gabriel.languages.cpp.Cpp11GradingLanguage;
import judgels.gabriel.languages.java.JavaGradingLanguage;
import judgels.gabriel.languages.pascal.PascalGradingLanguage;
import judgels.gabriel.languages.python.Python3GradingLanguage;

public class GradingLanguageRegistry {
    private static final GradingLanguageRegistry INSTANCE = new GradingLanguageRegistry();

    private static final Set<GradingLanguage> LANGUAGES = ImmutableSet.of(
            new CGradingLanguage(),
            new Cpp11GradingLanguage(),
            new CGradingLanguage(),
            new JavaGradingLanguage(),
            new PascalGradingLanguage(),
            new Python3GradingLanguage(),
            new OutputOnlyGradingLanguage());

    private static final Map<String, GradingLanguage> LANGUAGES_BY_SIMPLE_NAME = LANGUAGES.stream().collect(
            Collectors.toMap(GradingLanguageRegistry::getSimpleName, Function.identity()));

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
        return Maps.transformValues(LANGUAGES_BY_SIMPLE_NAME, GradingLanguage::getName);
    }

    private static String getSimpleName(GradingLanguage language) {
        String name = language.getClass().getSimpleName();
        return name.substring(0, name.length() - "GradingLanguage".length());
    }
}
