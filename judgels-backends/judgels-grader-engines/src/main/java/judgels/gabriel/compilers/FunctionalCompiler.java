package judgels.gabriel.compilers;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import judgels.gabriel.api.CompilationException;
import judgels.gabriel.api.CompilationResult;
import judgels.gabriel.api.Compiler;
import judgels.gabriel.api.GradingLanguage;
import judgels.gabriel.api.PreparationException;
import judgels.gabriel.api.Sandbox;
import judgels.gabriel.api.SandboxExecutionResult;
import judgels.gabriel.api.SandboxExecutionStatus;
import judgels.gabriel.api.SubmissionSource;
import org.apache.commons.io.FileUtils;

public class FunctionalCompiler implements Compiler {
    private static final String COMPILATION_OUTPUT_FILENAME = "_compilation.out";
    private static final String GRADER = "grader";

    private Sandbox sandbox;
    private File compilationDir;
    private GradingLanguage language;
    private Map<String, File> helperFiles;
    private String graderFilename;

    public void prepare(Sandbox sandbox, File compilationDir, GradingLanguage language, Map<String, File> helperFiles)
            throws PreparationException {

        this.graderFilename = GRADER + "." + language.getAllowedExtensions().get(0);
        if (!helperFiles.containsKey(this.graderFilename)) {
            throw new PreparationException(this.graderFilename + " is missing");
        }

        sandbox.setTimeLimitInMilliseconds(20 * 1000);
        sandbox.setMemoryLimitInKilobytes(1024 * 1024);

        sandbox.resetRedirections();
        sandbox.redirectStandardOutput(COMPILATION_OUTPUT_FILENAME);
        sandbox.redirectStandardError(COMPILATION_OUTPUT_FILENAME);

        this.sandbox = sandbox;
        this.compilationDir = compilationDir;
        this.helperFiles = helperFiles;
        this.language = language;
    }

    @Override
    public CompilationResult compile(Map<String, File> sourceFiles) throws CompilationException {
        for (Map.Entry<String, File> entry : helperFiles.entrySet()) {
            if (startsWithSourceFileFieldKey(entry.getKey(), sourceFiles)) {
                sandbox.addFile(entry.getValue());
            }
        }

        List<String> sourceFilenames = new ArrayList<>();
        for (Map.Entry<String, File> entry : sourceFiles.entrySet()) {
            sourceFilenames.add(entry.getValue().getName());
            sandbox.addFile(entry.getValue());
        }
        String[] sourceFilenamesArray = sourceFilenames.toArray(new String[0]);

        List<String> command = language.getCompilationCommand(graderFilename, sourceFilenamesArray);
        String executableFilename = language.getExecutableFilename(graderFilename);

        SandboxExecutionResult result = sandbox.execute(command);

        if (result.getStatus() == SandboxExecutionStatus.ZERO_EXIT_CODE) {
            File outputFile = sandbox.getFile(COMPILATION_OUTPUT_FILENAME);
            try {
                String compilationOutput = FileUtils.readFileToString(outputFile, StandardCharsets.UTF_8);
                FileUtils.forceDelete(outputFile);
                FileUtils.copyFileToDirectory(sandbox.getFile(executableFilename), compilationDir);
                return new CompilationResult.Builder()
                        .isSuccessful(true)
                        .putOutputs(SubmissionSource.DEFAULT_KEY, compilationOutput)
                        .build();
            } catch (IOException e) {
                throw new CompilationException(e);
            }
        }  else if (result.getStatus() == SandboxExecutionStatus.NONZERO_EXIT_CODE) {
            File outputFile = sandbox.getFile(COMPILATION_OUTPUT_FILENAME);
            try {
                String compilationOutput = FileUtils.readFileToString(outputFile, StandardCharsets.UTF_8);
                FileUtils.forceDelete(outputFile);
                return new CompilationResult.Builder()
                        .isSuccessful(false)
                        .putOutputs(SubmissionSource.DEFAULT_KEY, compilationOutput)
                        .build();
            } catch (IOException e) {
                throw new CompilationException(e);
            }
        } else {
            throw new CompilationException(String.join(" ", command) + " resulted in " + result);
        }
    }

    private boolean startsWithSourceFileFieldKey(String filename, Map<String, File> sourceFiles) {
        if (filename.startsWith(GRADER)) {
            return true;
        }
        for (String key : sourceFiles.keySet()) {
            if (filename.startsWith(key)) {
                return true;
            }
        }
        return false;
    }
}
