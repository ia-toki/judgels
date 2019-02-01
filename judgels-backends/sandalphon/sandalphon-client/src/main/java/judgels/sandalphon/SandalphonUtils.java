package judgels.sandalphon;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.ws.rs.ForbiddenException;
import judgels.gabriel.api.LanguageRestriction;
import judgels.gabriel.api.SubmissionSource;
import judgels.sandalphon.api.problem.ProblemInfo;
import judgels.sandalphon.api.problem.ProblemSubmissionConfig;

public class SandalphonUtils {
    private SandalphonUtils() {}

    public static void checkAllSourceFilesPresent(SubmissionSource source, ProblemSubmissionConfig config) {
        Set<String> missingSourceFiles =
                Sets.difference(config.getSourceKeys().keySet(), source.getSubmissionFiles().keySet());
        checkArgument(missingSourceFiles.isEmpty(), "Missing source files: {}", missingSourceFiles);
    }

    public static void checkGradingLanguageAllowed(String gradingLanguage, LanguageRestriction restriction) {
        checkGradingLanguageAllowed(gradingLanguage, ImmutableList.of(restriction));
    }

    public static void checkGradingLanguageAllowed(
            String gradingLanguage,
            LanguageRestriction r1,
            Optional<LanguageRestriction> r2) {

        ImmutableList.Builder<LanguageRestriction> restrictions = ImmutableList.builder();
        restrictions.add(r1);
        r2.ifPresent(restrictions::add);

        checkGradingLanguageAllowed(gradingLanguage, restrictions.build());
    }

    public static void checkGradingLanguageAllowed(String gradingLanguage, List<LanguageRestriction> restrictions) {
        LanguageRestriction finalRestriction = LanguageRestriction.noRestriction();
        for (LanguageRestriction restriction : restrictions) {
            finalRestriction = combineLanguageRestrictions(finalRestriction, restriction);
        }
        Set<String> allowedLanguages = finalRestriction.getAllowedLanguageNames();
        if (!allowedLanguages.isEmpty() && !allowedLanguages.contains(gradingLanguage)) {
            throw new ForbiddenException("Grading language " + gradingLanguage + " is not allowed");
        }
    }

    public static LanguageRestriction combineLanguageRestrictions(LanguageRestriction r1, LanguageRestriction r2) {
        if (r1.getAllowedLanguageNames().isEmpty()) {
            return r2;
        }
        if (r2.getAllowedLanguageNames().isEmpty()) {
            return r1;
        }
        return LanguageRestriction.of(Sets.intersection(r1.getAllowedLanguageNames(), r2.getAllowedLanguageNames()));
    }

    public static String getProblemName(ProblemInfo problem, Optional<String> language) {
        String finalLanguage = problem.getDefaultLanguage();
        if (language.isPresent() && problem.getTitlesByLanguage().containsKey(language.get())) {
            finalLanguage = language.get();
        }
        return problem.getTitlesByLanguage().get(finalLanguage);
    }
}
