package judgels.sandalphon;

import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.Set;
import javax.ws.rs.ForbiddenException;
import judgels.gabriel.api.LanguageRestriction;

public class SandalphonUtils {
    private SandalphonUtils() {}

    public static void checkGradingLanguageAllowed(String gradingLanguage, LanguageRestriction... restrictions) {
        Set<String> allowedLanguages = Collections.emptySet();
        for (LanguageRestriction restriction : restrictions) {
            if (allowedLanguages.isEmpty()) {
                allowedLanguages = restriction.getAllowedLanguageNames();
            } else {
                allowedLanguages = Sets.intersection(allowedLanguages, restriction.getAllowedLanguageNames());
            }
        }
        if (!allowedLanguages.isEmpty() && !allowedLanguages.contains(gradingLanguage)) {
            throw new ForbiddenException("Grading language " + gradingLanguage + " is not allowed");
        }
    }
}
