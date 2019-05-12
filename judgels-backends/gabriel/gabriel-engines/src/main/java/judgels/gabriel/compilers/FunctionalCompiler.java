package judgels.gabriel.compilers;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
import judgels.gabriel.languages.cpp.CppFamilyGradingLanguage;
import org.apache.commons.io.FileUtils;

public class FunctionalCompiler implements Compiler {
    private static final String COMPILATION_OUTPUT_FILENAME = "_compilation.out";
    private static final String GRADER = "grader";
    private static final String GRADER_FILENAME = "grader.cpp";

    private Sandbox sandbox;
    private File compilationDir;
    private CppFamilyGradingLanguage language;
    private Map<String, File> helperFiles;

    public void prepare(Sandbox sandbox, File compilationDir, GradingLanguage language, Map<String, File> helperFiles)
            throws PreparationException {

        if (!(language instanceof CppFamilyGradingLanguage)) {
            throw new PreparationException("Grading language must be of C++ family");
        }

        if (!helperFiles.containsKey(GRADER_FILENAME)) {
            throw new PreparationException(GRADER_FILENAME + " is missing");
        }

        sandbox.setTimeLimitInMilliseconds(10 * 1000);
        sandbox.setMemoryLimitInKilobytes(1024 * 1024);
        sandbox.setStackSizeInKilobytes(1024 * 1024);

        sandbox.resetRedirections();
        sandbox.redirectStandardOutput(COMPILATION_OUTPUT_FILENAME);
        sandbox.redirectStandardError(COMPILATION_OUTPUT_FILENAME);

        this.sandbox = sandbox;
        this.compilationDir = compilationDir;
        this.helperFiles = helperFiles;
        this.language = (CppFamilyGradingLanguage) language;
    }

    @Override
    public CompilationResult compile(Map<String, File> sourceFiles) throws CompilationException {
        for (Map.Entry<String, File> entry : helperFiles.entrySet()) {
            if (startsWithSourceFileFieldKey(entry.getKey(), sourceFiles) || entry.getKey().startsWith(GRADER)) {
                sandbox.addFile(entry.getValue());
            }
        }

        Map<String, String> outputs = Maps.newLinkedHashMap();
        boolean isSuccessful = compileSources(sourceFiles, outputs);
        if (!isSuccessful) {
            return new CompilationResult.Builder()
                    .isSuccessful(false)
                    .outputs(outputs)
                    .build();
        }

        ImmutableList.Builder<String> commandBuilder = ImmutableList.builder();
        commandBuilder.addAll(language.getCompilationCommand(GRADER_FILENAME));
        for (String sourceKey : sourceFiles.keySet()) {
            commandBuilder.add(sourceKey + ".o");
        }
        List<String> command = commandBuilder.build();
        SandboxExecutionResult result = sandbox.execute(command);
        isSuccessful = checkCompilationResult(command, result, outputs, GRADER, GRADER);

        return new CompilationResult.Builder()
                .isSuccessful(isSuccessful)
                .outputs(outputs)
                .build();
    }

    private boolean compileSources(Map<String, File> sourceFiles, Map<String, String> outputs)
            throws CompilationException {

        boolean isSuccessful = true;
        for (Map.Entry<String, File> entry : sourceFiles.entrySet()) {
            String objectFilename = entry.getKey() + ".o";
            String sourceFilename = entry.getValue().getName();

            List<String> command = language.getCompilationOnlyCommand(sourceFilename, objectFilename);

            sandbox.addFile(entry.getValue());
            SandboxExecutionResult result = sandbox.execute(command);
            if (!checkCompilationResult(command, result, outputs, entry.getKey(), objectFilename)) {
                isSuccessful = false;
            }
        }

        return isSuccessful;
    }

    private boolean startsWithSourceFileFieldKey(String filename, Map<String, File> sourceFiles) {
        for (String key : sourceFiles.keySet()) {
            if (filename.startsWith(key)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkCompilationResult(
            List<String> command,
            SandboxExecutionResult result,
            Map<String, String> outputs,
            String key,
            String resultFilename) throws CompilationException {

        if (result.getStatus() == SandboxExecutionStatus.ZERO_EXIT_CODE) {
            File outputFile = sandbox.getFile(COMPILATION_OUTPUT_FILENAME);
            try {
                String output = FileUtils.readFileToString(outputFile, StandardCharsets.UTF_8);
                FileUtils.forceDelete(outputFile);
                FileUtils.copyFileToDirectory(sandbox.getFile(resultFilename), compilationDir);
                outputs.put(key, output);
            } catch (IOException e) {
                throw new CompilationException(e);
            }
            return true;
        }  else if (result.getStatus() == SandboxExecutionStatus.NONZERO_EXIT_CODE) {
            File outputFile = sandbox.getFile(COMPILATION_OUTPUT_FILENAME);
            try {
                String output = FileUtils.readFileToString(outputFile, StandardCharsets.UTF_8);
                FileUtils.forceDelete(outputFile);
                outputs.put(key, output);
            } catch (IOException e) {
                throw new CompilationException(e);
            }
            return false;
        } else {
            throw new CompilationException(String.join(" ", command) + " resulted in " + result);
        }
    }
}
