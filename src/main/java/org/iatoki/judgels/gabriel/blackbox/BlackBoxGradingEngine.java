package org.iatoki.judgels.gabriel.blackbox;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.io.FileUtils;
import org.iatoki.judgels.gabriel.GabrielLogger;
import org.iatoki.judgels.gabriel.GradingEngine;
import org.iatoki.judgels.gabriel.GradingException;
import org.iatoki.judgels.gabriel.GradingLanguage;
import org.iatoki.judgels.gabriel.sandboxes.SandboxFactory;
import org.slf4j.MDC;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class BlackBoxGradingEngine implements GradingEngine {
    private File compilationDir;
    private File evaluationDir;
    private File scoringDir;

    private int compilationTimeLimit;
    private int compilationMemoryLimit;

    private Set<Integer> alreadyFailedSubtaskIds;

    protected BlackBoxGradingEngine() {
        this.compilationTimeLimit = 10000;
        this.compilationMemoryLimit = 1024 * 1024;
    }

    public final BlackBoxGradingResult gradeAfterInitialization(SandboxFactory sandboxFactory, File workingDir, GradingLanguage language, Map<String, File> sourceFiles, Map<String, File> helperFiles, Map<String, File> testDataFiles, BlackBoxGradingConfig config) throws GradingException {
        verifySourceFiles(sourceFiles, config);

        MDC.put("phase", "Preparation");

        GabrielLogger.getLogger().info("Preparation started.");
        prepare(sandboxFactory, workingDir, config, language, sourceFiles, helperFiles);
        GabrielLogger.getLogger().info("Preparation finished.");

        MDC.put("phase", "Compilation");

        GabrielLogger.getLogger().info("Compilation started.");
        CompilationResult compilationResult = getCompiler().compile();
        GabrielLogger.getLogger().info("Compilation finished.");

        Map<String, String> compilationOutput = compilationResult.getOutputs();

        if (compilationResult.getVerdict() == CompilationVerdict.COMPILATION_ERROR) {
            return BlackBoxGradingResult.compilationErrorResult(compilationOutput);
        }

        List<List<TestCaseResult>> testGroupResults = Lists.newArrayList();
        List<List<EvaluationResult>> testGroupEvaluationResults = Lists.newArrayList();

        List<List<TestCaseResult>> testCaseResultsBySubtaskIds = Lists.newArrayList();
        List<List<Integer[]>> testCaseIndicesBySubtaskIds = Lists.newArrayList();

        // +2 because: ids are 1-based, and we can have id=-1 (so we need additional +1 offset)
        for (int i = 0; i < config.getSubtasks().size() + 2; i++) {
            testCaseResultsBySubtaskIds.add(Lists.newArrayList());
            testCaseIndicesBySubtaskIds.add(Lists.newArrayList());
        }

        MDC.put("phase", "Evaluation & Scoring");


        alreadyFailedSubtaskIds = Sets.newHashSet();

        GabrielLogger.getLogger().info("Evaluation & scoring started.");
        for (int i = 0; i < config.getTestData().size(); i++) {
            TestGroup testGroup = config.getTestData().get(i);

            testGroupResults.add(Lists.newArrayList());
            testGroupEvaluationResults.add(Lists.newArrayList());

            for (int j = 0; j < testGroup.getTestCases().size(); j++) {
                TestCase testCase = testGroup.getTestCases().get(j);

                File testCaseInput = testDataFiles.get(testCase.getInput());
                File testCaseOutput = testDataFiles.get(testCase.getOutput());

                EvaluationResult evaluationResult;

                if (alreadyFailedSubtaskIds.containsAll(testCase.getSubtaskIds())) {
                    evaluationResult = EvaluationResult.skippedResult();
                } else {
                    evaluationResult = getEvaluator().evaluate(testCaseInput);
                }

                TestCaseResult testCaseResult;
                if (evaluationResult.getVerdict() == EvaluationVerdict.OK) {
                    ScoringResult scoringResult = getScorer().score(testCaseInput, testCaseOutput);
                    testCaseResult = TestCaseResult.fromScoringResult(scoringResult);
                } else {
                    testCaseResult = TestCaseResult.fromEvaluationResult(evaluationResult);
                }

                if (testCaseResult.getVerdict() != ScoringVerdict.ACCEPTED) {
                    alreadyFailedSubtaskIds.addAll(testCase.getSubtaskIds());
                    alreadyFailedSubtaskIds.remove(0);
                    alreadyFailedSubtaskIds.remove(-1);
                }

                testGroupResults.get(i).add(testCaseResult);
                testGroupEvaluationResults.get(i).add(evaluationResult);

                for (int subtaskId : testCase.getSubtaskIds()) {
                    testCaseResultsBySubtaskIds.get(subtaskId + 1).add(testCaseResult);
                    testCaseIndicesBySubtaskIds.get(subtaskId + 1).add(new Integer[]{i, j});
                }
            }
        }
        GabrielLogger.getLogger().info("Evaluation & scoring finished.");

        MDC.put("phase", "Reduction");

        GabrielLogger.getLogger().info("Reduction started.");
        ImmutableList.Builder<SubtaskResult> subtaskResultsBuilder = ImmutableList.builder();
        for (Subtask subtask : config.getSubtasks()) {
            List<TestCaseResult> testCaseResults = ImmutableList.copyOf(testCaseResultsBySubtaskIds.get(subtask.getId() + 1));
            SubtaskResult subtaskResult = getReducer().reduceTestCaseResults(testCaseResults, subtask);
            subtaskResultsBuilder.add(subtaskResult);

            List<TestCaseResult> improvedTestCaseResults = getReducer().improveTestCaseResults(testCaseResults, subtask);

            for (int i = 0; i < testCaseResults.size(); i++) {
                Integer[] testCaseIndices = testCaseIndicesBySubtaskIds.get(subtask.getId() + 1).get(i);
                int testGroupIndex = testCaseIndices[0];
                int testCaseIndex = testCaseIndices[1];
                testGroupResults.get(testGroupIndex).set(testCaseIndex, improvedTestCaseResults.get(i));
            }
        }

        List<SubtaskResult> subtaskResults = subtaskResultsBuilder.build();
        ReductionResult result = getReducer().reduceSubtaskResults(subtaskResults);
        GabrielLogger.getLogger().info("Reduction finished.");

        ImmutableList.Builder<SubtaskFinalResult> subtaskFinalResults = ImmutableList.builder();
        for (int i = 0; i < config.getSubtasks().size(); i++) {
            Subtask subtask = config.getSubtasks().get(i);
            subtaskFinalResults.add(new SubtaskFinalResult(subtask.getId(), subtaskResults.get(i)));
        }

        ImmutableList.Builder<TestGroupFinalResult> testGroupFinalResults = ImmutableList.builder();
        for (int i = 0; i < config.getTestData().size(); i++) {
            TestGroup testGroup = config.getTestData().get(i);
            ImmutableList.Builder<TestCaseFinalResult> testCaseFinalResults = ImmutableList.builder();

            for (int j = 0; j < testGroup.getTestCases().size(); j++) {
                TestCase testCase = testGroup.getTestCases().get(j);
                testCaseFinalResults.add(new TestCaseFinalResult(testGroupResults.get(i).get(j), testGroupEvaluationResults.get(i).get(j).getExecutionResult(), testCase.getSubtaskIds()));
            }
            testGroupFinalResults.add(new TestGroupFinalResult(testGroup.getId(), testCaseFinalResults.build()));
        }

        BlackBoxGradingResultDetails details = new BlackBoxGradingResultDetails(compilationOutput, testGroupFinalResults.build(), subtaskFinalResults.build());

        return BlackBoxGradingResult.normalResult(result, details);
    }

    protected final File getCompilationDir() {
        return compilationDir;
    }

    protected final File getEvaluationDir() {
        return evaluationDir;
    }

    protected final File getScoringDir() {
        return scoringDir;
    }

    protected final int getCompilationTimeLimitInMilliseconds() {
        return compilationTimeLimit;
    }

    protected final int getCompilationMemoryLimitInKilobytes() {
        return compilationMemoryLimit;
    }

    protected final int getDefaultCompilationTimeLimitInMilliseconds() {
        return 2000;
    }

    protected final int getDefaultMemoryLimitInKilobytes() {
        return 65536;
    }

    public final void setCompilationTimeLimitInMilliseconds(int compilationTimeLimit) {
        this.compilationTimeLimit = compilationTimeLimit;
    }

    public final void setCompilationMemoryLimitInKilobytes(int compilationMemoryLimit) {
        this.compilationMemoryLimit = compilationMemoryLimit;
    }

    protected final void prepareWorkingDirs(File workingDir) throws PreparationException {
        try {
            compilationDir = new File(workingDir, "compilation");
            FileUtils.forceMkdir(compilationDir);
            evaluationDir = new File(workingDir, "evaluation");
            FileUtils.forceMkdir(evaluationDir);
            scoringDir = new File(workingDir, "scoring");
            FileUtils.forceMkdir(scoringDir);
        } catch (IOException e) {
            throw new PreparationException("Cannot make directories inside " + workingDir.getAbsolutePath());
        }
    }

    protected abstract void prepare(SandboxFactory sandboxFactory, File workingDir, BlackBoxGradingConfig config, GradingLanguage language, Map<String, File> sourceFiles, Map<String, File> helperFiles) throws PreparationException;

    protected abstract Compiler getCompiler();

    protected abstract Evaluator getEvaluator();

    protected abstract Scorer getScorer();

    protected abstract Reducer getReducer();

    private void verifySourceFiles(Map<String, File> sourceFiles, BlackBoxGradingConfig config) throws PreparationException {
        for (String fieldKey : config.getSourceFileFields().keySet()) {
            if (!sourceFiles.containsKey(fieldKey)) {
                throw new PreparationException("No source file found for '" + fieldKey + "'");
            }
        }
    }
}
