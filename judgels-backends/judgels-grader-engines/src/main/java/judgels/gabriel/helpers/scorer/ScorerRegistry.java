package judgels.gabriel.helpers.scorer;

import jakarta.annotation.Nullable;
import java.io.File;
import java.util.Map;
import java.util.Optional;
import judgels.gabriel.api.PreparationException;
import judgels.gabriel.api.Sandbox;
import judgels.gabriel.api.Scorer;
import judgels.gabriel.languages.cpp.Cpp17GradingLanguage;
import judgels.gabriel.languages.cpp.CppFamilyGradingLanguage;

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
