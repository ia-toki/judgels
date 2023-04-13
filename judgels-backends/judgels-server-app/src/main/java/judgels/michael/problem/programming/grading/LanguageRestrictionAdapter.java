package judgels.michael.problem.programming.grading;

import java.util.LinkedHashSet;
import java.util.Set;
import judgels.gabriel.api.LanguageRestriction;
import judgels.gabriel.languages.GradingLanguageRegistry;

public class LanguageRestrictionAdapter {
    private LanguageRestrictionAdapter() {}

    public static Set<String> getAllowedLanguages(LanguageRestriction languageRestriction) {
        Set<String> languages = new LinkedHashSet<>(GradingLanguageRegistry.getInstance().getVisibleLanguages().keySet());
        if (!languageRestriction.isAllowedAll()) {
            languages.retainAll(languageRestriction.getAllowedLanguages());
        }
        return languages;
    }

    public static LanguageRestriction getLanguageRestriction(boolean isAllowedAll, Set<String> allowedLanguages) {
        if (isAllowedAll) {
            return LanguageRestriction.noRestriction();
        }
        return LanguageRestriction.of(allowedLanguages);
    }
}
