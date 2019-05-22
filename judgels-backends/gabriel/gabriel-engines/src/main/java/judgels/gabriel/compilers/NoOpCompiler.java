package judgels.gabriel.compilers;

import java.io.File;
import java.util.Map;
import judgels.gabriel.api.CompilationResult;
import judgels.gabriel.api.Compiler;
import judgels.gabriel.api.SubmissionSource;

public class NoOpCompiler implements Compiler {
    @Override
    public CompilationResult compile(Map<String, File> sourceFiles) {
        return new CompilationResult.Builder()
                .isSuccessful(true)
                .putOutputs(SubmissionSource.DEFAULT_KEY, "")
                .build();
    }
}
