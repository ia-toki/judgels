package org.iatoki.judgels.gabriel.blackbox;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.io.FileUtils;
import org.iatoki.judgels.gabriel.GabrielLogger;
import org.iatoki.judgels.gabriel.GradingConfig;
import org.iatoki.judgels.gabriel.GradingEngine;
import org.iatoki.judgels.gabriel.GradingException;
import org.iatoki.judgels.gabriel.GradingLanguage;
import org.iatoki.judgels.gabriel.GradingResult;
import org.iatoki.judgels.gabriel.GradingSource;
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

    private File gradingDir;
    private BlackBoxGradingConfig config;
    private GradingLanguage language;
    private GradingSource source;
    private SandboxFactory sandboxFactory;

    private CompilationResult compilationResult;

    private List<List<TestCaseResult>> testGroupResults;
    private List<List<EvaluationResult>> testGroupEvaluationResults;

    private List<SubtaskResult> subtaskResults;
    private ReductionResult reductionResult;

    private List<List<TestCaseResult>> testCaseResultsBySubtaskIds;
    private List<List<Integer[]>> testCaseIndicesBySubtaskIds;

    protected BlackBoxGradingEngine() {
        this.compilationTimeLimit = 10000;
        this.compilationMemoryLimit = 1024 * 1024;
    }

    @Override
    public GradingResult grade(File gradingDir, GradingConfig config, GradingLanguage language, GradingSource source, SandboxFactory sandboxFactory) throws GradingException {
        this.gradingDir = gradingDir;
        this.config = (BlackBoxGradingConfig) config;
        this.language = language;
        this.source = source;
        this.sandboxFactory = sandboxFactory;

        try {
            return tryGrading();
        } finally {
            cleanUp();

            MDC.remove("gradingPhase");
        }
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

    protected abstract void prepareAlgorithms(BlackBoxGradingConfig config, GradingLanguage language, Map<String, File> sourceFiles, Map<String, File> helperFiles, SandboxFactory sandboxFactory) throws PreparationException;

    protected abstract void cleanUp();

    protected abstract Compiler getCompiler();

    protected abstract Evaluator getEvaluator();

    protected abstract Scorer getScorer();

    protected abstract Reducer getReducer();

    private GradingResult tryGrading() throws GradingException {
        verify();
        prepare();
        compile();

        if ((getCompiler() != null) && (compilationResult.getVerdict() == CompilationVerdict.COMPILATION_ERROR)) {
            return BlackBoxGradingResults.compilationErrorResult(compilationResult.getOutputs());
        }

        evaluateAndScore();
        reduce();

        return buildResult();
    }

    private void verify() throws VerificationException {
        MDC.put("gradingPhase", "VERIFY");
        GabrielLogger.getLogger().info("Verification started.");
        for (String fieldKey : config.getSourceFileFields().keySet()) {
            if (!source.getSourceFiles().containsKey(fieldKey)) {
                throw new VerificationException("No source file found for '" + fieldKey + "'");
            }
        }
        GabrielLogger.getLogger().info("Verification finished.");
    }

    private void prepare() throws PreparationException {
        MDC.put("gradingPhase", "PREPARE");
        GabrielLogger.getLogger().info("Preparation started.");
        prepareGradingDirs();
        prepareAlgorithms(config, language, source.getSourceFiles(), source.getHelperFiles(), sandboxFactory);
        GabrielLogger.getLogger().info("Preparation finished.");
    }

    private void prepareGradingDirs() throws PreparationException {
        try {
            compilationDir = new File(gradingDir, "compilation");
            FileUtils.forceMkdir(compilationDir);
            evaluationDir = new File(gradingDir, "evaluation");
            FileUtils.forceMkdir(evaluationDir);
            scoringDir = new File(gradingDir, "scoring");
            FileUtils.forceMkdir(scoringDir);
        } catch (IOException e) {
            throw new PreparationException(e);
        }
    }

    private void compile() throws CompilationException {
        MDC.put("gradingPhase", "COMPILE");
        GabrielLogger.getLogger().info("Compilation started.");
        if (getCompiler() != null) {
            compilationResult = getCompiler().compile();
        } else {
            GabrielLogger.getLogger().info("No compiler is used.");
        }
        GabrielLogger.getLogger().info("Compilation finished.");
    }

    private void evaluateAndScore() throws EvaluationException, ScoringException {
        MDC.put("gradingPhase", "EVALUATE-SCORE");
        GabrielLogger.getLogger().info("Evaluation & scoring started.");

        testGroupResults = Lists.newArrayList();
        testGroupEvaluationResults = Lists.newArrayList();

        testCaseResultsBySubtaskIds = Lists.newArrayList();
        testCaseIndicesBySubtaskIds = Lists.newArrayList();

        // +2 because: ids are 1-based, and we can have id=-1 (so we need additional +1 offset)
        for (int i = 0; i < config.getSubtasks().size() + 2; i++) {
            testCaseResultsBySubtaskIds.add(Lists.newArrayList());
            testCaseIndicesBySubtaskIds.add(Lists.newArrayList());
        }

        Set<Integer> alreadyFailedSubtaskIds = Sets.newHashSet();

        for (int i = 0; i < config.getTestData().size(); i++) {
            TestGroup testGroup = config.getTestData().get(i);

            testGroupResults.add(Lists.newArrayList());
            testGroupEvaluationResults.add(Lists.newArrayList());

            for (int j = 0; j < testGroup.getTestCases().size(); j++) {
                TestCase testCase = testGroup.getTestCases().get(j);

                File testCaseInput = source.getTestDataFiles().get(testCase.getInput());
                File testCaseOutput = source.getTestDataFiles().get(testCase.getOutput());

                EvaluationResult evaluationResult;

                if (alreadyFailedSubtaskIds.containsAll(testCase.getSubtaskIds())) {
                    evaluationResult = EvaluationResult.skippedResult();
                } else {
                    evaluationResult = getEvaluator().evaluate(testCaseInput);
                }

                TestCaseResult testCaseResult;
                if (evaluationResult.getVerdict() == EvaluationVerdict.OK) {
                    ScoringResult scoringResult = getScorer().score(testCaseInput, testCaseOutput, new File(getEvaluationDir(), getEvaluator().getEvaluationResultFilename(testCaseInput)));
                    testCaseResult = TestCaseResult.fromScoringResult(scoringResult);
                } else {
                    testCaseResult = TestCaseResult.fromEvaluationResult(evaluationResult);
                }

                if (testCaseResult.getVerdict() != ScoringVerdict.ACCEPTED && testCaseResult.getVerdict() != ScoringVerdict.OK) {
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
    }

    private void reduce() throws ReductionException {
        MDC.put("gradingPhase", "REDUCE");
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

        subtaskResults = subtaskResultsBuilder.build();
        reductionResult = getReducer().reduceSubtaskResults(subtaskResults);
        GabrielLogger.getLogger().info("Reduction finished.");
    }

    private GradingResult buildResult() {
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

        Map<String, String> compilationOutput;
        if (getCompiler() != null) {
            compilationOutput = compilationResult.getOutputs();
        } else {
            compilationOutput = ImmutableMap.of("source", "");
        }

        BlackBoxGradingResultDetails details = BlackBoxGradingResultDetails.normalDetails(compilationOutput, testGroupFinalResults.build(), subtaskFinalResults.build());

        return BlackBoxGradingResults.normalResult(reductionResult, details);
    }
}
