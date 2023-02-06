package org.iatoki.judgels.sandalphon.problem.programming.grading;

import com.google.common.collect.Sets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import judgels.gabriel.api.LanguageRestriction;
import judgels.gabriel.languages.GradingLanguageRegistry;

public final class LanguageRestrictionAdapter {

    private LanguageRestrictionAdapter() {
        // prevent instantiation
    }

    public static LanguageRestriction createLanguageRestrictionFromForm(Map<String, String> allowedLanguageNames, boolean isAllowedAll) {
        if (allowedLanguageNames == null || isAllowedAll) {
            return LanguageRestriction.noRestriction();
        } else {
            return LanguageRestriction.of(allowedLanguageNames.keySet());
        }
    }

    public static boolean getFormIsAllowedAllFromLanguageRestriction(LanguageRestriction languageRestriction) {
        return languageRestriction.isAllowedAll();
    }

    public static Map<String, String> getFormAllowedLanguageNamesFromLanguageRestriction(LanguageRestriction languageRestriction) {
        return languageRestriction.getAllowedLanguages().stream().collect(Collectors.toMap(e -> e, e -> e));
    }

    public static Set<String> getFinalAllowedLanguageNames(List<LanguageRestriction> languageRestrictions) {
        Set<String> result = Sets.newHashSet(GradingLanguageRegistry.getInstance().getVisibleNamesMap().keySet());

        for (LanguageRestriction languageRestriction : languageRestrictions) {
            if (!languageRestriction.isAllowedAll()) {
                result.retainAll(languageRestriction.getAllowedLanguages());
            }
        }

        return result;
    }
}
