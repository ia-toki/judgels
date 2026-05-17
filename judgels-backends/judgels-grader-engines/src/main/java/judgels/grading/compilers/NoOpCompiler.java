package judgels.grading.compilers;

import java.io.File;
import java.util.Map;
import judgels.grading.api.CompilationResult;
import judgels.grading.api.Compiler;
import judgels.grading.api.SubmissionSource;

public class NoOpCompiler implements Compiler {
    @Override
    public CompilationResult compile(Map<String, File> sourceFiles) {
        return new CompilationResult.Builder()
                .isSuccessful(true)
                .putOutputs(SubmissionSource.DEFAULT_KEY, "")
                .build();
    }
}
