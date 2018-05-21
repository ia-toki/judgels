package judgels.sandalphon;

import com.google.common.collect.Sets;
import java.util.Set;
import javax.ws.rs.ForbiddenException;
import judgels.gabriel.api.LanguageRestriction;

public class SandalphonUtils {
    private SandalphonUtils() {}

    public static void checkGradingLanguageAllowed(String gradingLanguage, LanguageRestriction... restrictions) {
        LanguageRestriction finalRestriction = LanguageRestriction.noRestriction();
        for (LanguageRestriction restriction : restrictions) {
            finalRestriction = combineLanguageRestrictions(finalRestriction, restriction);
        }
        Set<String> allowedLanguages = finalRestriction.getAllowedLanguages();
        if (!allowedLanguages.isEmpty() && !allowedLanguages.contains(gradingLanguage)) {
            throw new ForbiddenException("Grading language " + gradingLanguage + " is not allowed");
        }
    }

    public static LanguageRestriction combineLanguageRestrictions(LanguageRestriction r1, LanguageRestriction r2) {
        if (r1.getAllowedLanguages().isEmpty()) {
            return r2;
        }
        if (r2.getAllowedLanguages().isEmpty()) {
            return r1;
        }
        return LanguageRestriction.of(Sets.intersection(r1.getAllowedLanguages(), r2.getAllowedLanguages()));
    }
}
