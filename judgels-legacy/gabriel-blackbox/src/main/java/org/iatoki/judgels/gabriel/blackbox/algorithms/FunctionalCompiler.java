package org.iatoki.judgels.gabriel.blackbox.algorithms;

import com.beust.jcommander.internal.Maps;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import org.apache.commons.io.FileUtils;
import org.iatoki.judgels.gabriel.GradingLanguage;
import org.iatoki.judgels.gabriel.blackbox.CompilationException;
import org.iatoki.judgels.gabriel.blackbox.CompilationResult;
import org.iatoki.judgels.gabriel.blackbox.CompilationVerdict;
import org.iatoki.judgels.gabriel.blackbox.Compiler;
import org.iatoki.judgels.gabriel.blackbox.PreparationException;
import org.iatoki.judgels.gabriel.blackbox.languages.PlainCppGradingLanguage;
import org.iatoki.judgels.gabriel.sandboxes.Sandbox;
import org.iatoki.judgels.gabriel.sandboxes.SandboxExecutionResult;
import org.iatoki.judgels.gabriel.sandboxes.SandboxExecutionStatus;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class FunctionalCompiler implements Compiler {

    private static final String COMPILATION_OUTPUT_FILENAME = "_compilation.out";
    private static final String GRADER = "grader";

    private final GradingLanguage gradingLanguage;

    private final Sandbox sandbox;
    private final File compilationDir;
    private final Map<String, File> sourceFiles;
    private final Map<String, File> helperFiles;

    public FunctionalCompiler(Sandbox sandbox, File compilationDir, GradingLanguage gradingLanguage, Map<String, File> sourceFiles, Map<String, File> helperFiles, int timeLimitInMilliseconds, int memoryLimitInKilobytes) throws PreparationException {
        this.gradingLanguage = gradingLanguage;
        this.sourceFiles = sourceFiles;
        this.helperFiles = helperFiles;

        this.sandbox = sandbox;
        copyFilesToSandbox();

        sandbox.setTimeLimitInMilliseconds(timeLimitInMilliseconds);
        sandbox.setMemoryLimitInKilobytes(memoryLimitInKilobytes);
        sandbox.setStackSizeInKilobytes(memoryLimitInKilobytes);

        sandbox.resetRedirections();
        sandbox.redirectStandardOutput(COMPILATION_OUTPUT_FILENAME);
        sandbox.redirectStandardError(COMPILATION_OUTPUT_FILENAME);

        this.compilationDir = compilationDir;
    }

    @Override
    public CompilationResult compile() throws CompilationException {
        Map<String, String> outputs = Maps.newLinkedHashMap();

        CompilationVerdict verdict1 = compileSources(outputs);
        CompilationVerdict verdict2 = compileGrader();
        CompilationVerdict finalVerdict = verdict1 == CompilationVerdict.OK && verdict2 == CompilationVerdict.OK
                ? CompilationVerdict.OK : CompilationVerdict.COMPILATION_ERROR;

        return new CompilationResult(finalVerdict, outputs);
    }

    private void copyFilesToSandbox() throws PreparationException {
        if (!helperFiles.containsKey(GRADER + ".cpp")) {
            throw new PreparationException(GRADER + ".cpp is missing");
        }

        for (Map.Entry<String, File> entry : helperFiles.entrySet()) {
            if (startsWithSourceFileFieldKey(entry.getKey()) || entry.getKey().startsWith(GRADER)) {
                sandbox.addFile(entry.getValue());
            }
        }

        for (File sourceFile : sourceFiles.values()) {
            sandbox.addFile(sourceFile);
        }
    }

    private boolean startsWithSourceFileFieldKey(String filename) {
        for (String key : sourceFiles.keySet()) {
            if (filename.startsWith(key)) {
                return true;
            }
        }
        return false;
    }

    private CompilationVerdict compileSources(Map<String, String> outputs) throws CompilationException {
        CompilationVerdict compilationVerdict = CompilationVerdict.OK;

        for (Map.Entry<String, File> entry : sourceFiles.entrySet()) {
            String executableFilename = entry.getKey() + ".o";
            String sourceFilename = entry.getValue().getName();

            final List<String> compilationCommand;
            if (gradingLanguage instanceof PlainCppGradingLanguage) {
                compilationCommand = ImmutableList.of(
                        "/usr/bin/g++", "-o", executableFilename, "-c", sourceFilename);
            } else {
                compilationCommand = ImmutableList.of(
                        "/usr/bin/g++", "-std=c++11", "-o", executableFilename, "-c", sourceFilename, "-O2");
            }
            SandboxExecutionResult executionResult = sandbox.execute(compilationCommand);

            if (executionResult.getStatus() == SandboxExecutionStatus.ZERO_EXIT_CODE) {
                File compilationOutputFile = sandbox.getFile(COMPILATION_OUTPUT_FILENAME);
                try {
                    String compilationOutput = FileUtils.readFileToString(compilationOutputFile);
                    FileUtils.forceDelete(compilationOutputFile);
                    FileUtils.copyFileToDirectory(sandbox.getFile(executableFilename), compilationDir);
                    outputs.put(entry.getKey(), compilationOutput);
                } catch (IOException e) {
                    throw new CompilationException(e.getMessage());
                }
            }  else if (executionResult.getStatus() == SandboxExecutionStatus.NONZERO_EXIT_CODE) {
                File compilationOutputFile = sandbox.getFile(COMPILATION_OUTPUT_FILENAME);
                try {
                    String compilationOutput = FileUtils.readFileToString(compilationOutputFile);
                    FileUtils.forceDelete(compilationOutputFile);
                    outputs.put(entry.getKey(), compilationOutput);
                    compilationVerdict = CompilationVerdict.COMPILATION_ERROR;
                } catch (IOException e) {
                    throw new CompilationException(e.getMessage());
                }
            } else {
                throw new CompilationException(Joiner.on(" ").join(compilationCommand) + " resulted in " + executionResult);
            }
        }

        return compilationVerdict;
    }

    private CompilationVerdict compileGrader() {
        ImmutableList.Builder<String> compilationCommandBuilder = ImmutableList.builder();
        if (gradingLanguage instanceof PlainCppGradingLanguage) {
            compilationCommandBuilder.add("/usr/bin/g++", "-o", GRADER, GRADER + ".cpp");
        } else {
            compilationCommandBuilder.add("/usr/bin/g++", "-std=c++11", "-o", GRADER, GRADER + ".cpp", "-O2", "-lm");
        }
        for (String sourceKey : sourceFiles.keySet()) {
            compilationCommandBuilder.add(sourceKey + ".o");
        }
        List<String> compilationCommand = compilationCommandBuilder.build();
        SandboxExecutionResult executionResult = sandbox.execute(compilationCommand);

        if (executionResult.getStatus() == SandboxExecutionStatus.ZERO_EXIT_CODE) {
            File compilationOutputFile = sandbox.getFile(COMPILATION_OUTPUT_FILENAME);
            try {
                FileUtils.forceDelete(compilationOutputFile);
                FileUtils.copyFileToDirectory(sandbox.getFile(GRADER), compilationDir);
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
        }  else if (executionResult.getStatus() == SandboxExecutionStatus.NONZERO_EXIT_CODE) {
            File compilationOutputFile = sandbox.getFile(COMPILATION_OUTPUT_FILENAME);
            try {
                FileUtils.forceDelete(compilationOutputFile);
                return CompilationVerdict.COMPILATION_ERROR;
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
        } else {
            throw new RuntimeException(Joiner.on(" ").join(compilationCommand) + " resulted in " + executionResult);
        }

        return CompilationVerdict.OK;
    }
}
