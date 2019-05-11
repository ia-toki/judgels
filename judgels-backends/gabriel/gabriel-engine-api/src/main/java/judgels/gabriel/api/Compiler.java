package judgels.gabriel.api;

import java.io.File;
import java.util.Map;

public interface Compiler {
    void prepare(
            Sandbox sandbox,
            File compilationDir,
            GradingLanguage language,
            Map<String, File> helperFiles,
            int timeLimitInMilliseconds,
            int memoryLimitInKilobytes) throws PreparationException;

    CompilationResult compile(Map<String, File> sourceFiles) throws CompilationException;
}
