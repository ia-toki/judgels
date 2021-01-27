package judgels.sandalphon.submission.programming;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.collect.Sets;
import java.util.Set;
import javax.ws.rs.ForbiddenException;
import judgels.gabriel.api.LanguageRestriction;
import judgels.gabriel.api.OutputOnlyOverrides;
import judgels.gabriel.api.SubmissionSource;
import judgels.sandalphon.api.problem.programming.ProblemSubmissionConfig;

public class SubmissionUtils {
    private SubmissionUtils() {}

    public static void checkAllSourceFilesPresent(SubmissionSource source, ProblemSubmissionConfig config) {
        Set<String> missingSourceFiles =
                Sets.difference(config.getSourceKeys().keySet(), source.getSubmissionFiles().keySet());
        checkArgument(missingSourceFiles.isEmpty(), "Missing source files: %s", missingSourceFiles);
    }

    public static void checkGradingLanguageAllowed(
            String gradingEngine,
            String gradingLanguage,
            LanguageRestriction restriction) {

        boolean allowed;
        if (gradingEngine.startsWith(OutputOnlyOverrides.KEY)) {
            allowed = gradingLanguage.startsWith(OutputOnlyOverrides.KEY);
        } else if (gradingLanguage.startsWith(OutputOnlyOverrides.KEY)) {
            allowed = gradingEngine.startsWith(OutputOnlyOverrides.KEY);
        } else {
            allowed = restriction.isAllowedAll() || restriction.getAllowedLanguages().contains(gradingLanguage);
        }

        if (!allowed) {
            throw new ForbiddenException("Grading language " + gradingLanguage + " is not allowed");
        }
    }
}
