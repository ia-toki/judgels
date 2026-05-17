package judgels.grading.helpers.scorer;

import jakarta.annotation.Nullable;
import java.io.File;
import java.util.Map;
import java.util.Optional;
import judgels.grading.api.PreparationException;
import judgels.grading.api.Sandbox;
import judgels.grading.api.Scorer;
import judgels.grading.languages.cpp.Cpp17GradingLanguage;
import judgels.grading.languages.cpp.CppFamilyGradingLanguage;

public class ScorerRegistry {
    private static final CppFamilyGradingLanguage language = new Cpp17GradingLanguage();

    private ScorerRegistry() {}

    public static Scorer getAndPrepare(
            Optional<String> customScorer,
            Map<String, File> helperFiles,
            @Nullable Sandbox sandbox,
            File evaluationDir) throws PreparationException {

        if (customScorer.isPresent()) {
            File scorerFile = helperFiles.get(customScorer.get());
            CustomScorer scorer = new CustomScorer();
            scorer.prepare(sandbox, evaluationDir, language, scorerFile);
            return scorer;
        } else {
            return new DiffScorer(evaluationDir);
        }
    }
}
