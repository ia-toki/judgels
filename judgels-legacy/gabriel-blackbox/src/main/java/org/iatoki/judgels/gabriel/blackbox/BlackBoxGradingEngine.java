package org.iatoki.judgels.gabriel.blackbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import judgels.gabriel.aggregators.SubtaskAggregator;
import judgels.gabriel.api.CompilationException;
import judgels.gabriel.api.CompilationResult;
import judgels.gabriel.api.Compiler;
import judgels.gabriel.api.EvaluationException;
import judgels.gabriel.api.EvaluationResult;
import judgels.gabriel.api.Evaluator;
import judgels.gabriel.api.GradingException;
import judgels.gabriel.api.GradingLanguage;
import judgels.gabriel.api.GradingResult;
import judgels.gabriel.api.GradingSource;
import judgels.gabriel.api.PreparationException;
import judgels.gabriel.api.SandboxFactory;
import judgels.gabriel.api.ScoringException;
import judgels.gabriel.api.SubtaskVerdict;
import judgels.gabriel.api.TestCaseAggregationResult;
import judgels.gabriel.api.TestCaseAggregator;
import judgels.gabriel.api.GradingConfig;
import judgels.gabriel.api.Subtask;
import judgels.gabriel.api.TestCase;
import judgels.gabriel.api.TestCaseVerdict;
import judgels.gabriel.api.TestGroup;
import judgels.gabriel.api.Verdict;
import judgels.gabriel.api.VerificationException;
import org.apache.commons.io.FileUtils;
import org.iatoki.judgels.gabriel.GabrielLogger;
import org.iatoki.judgels.gabriel.GradingEngine;
import org.iatoki.judgels.gabriel.sandboxes.SandboxExecutionResult;
import org.iatoki.judgels.gabriel.sandboxes.SandboxExecutionStatus;
import org.slf4j.MDC;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class BlackBoxGradingEngine implements GradingEngine {
    protected static final ObjectMapper MAPPER = new ObjectMapper().registerModules(new Jdk8Module(), new GuavaModule());

    private File compilationDir;
    private File evaluationDir;
    private File scoringDir;

    private int compilationTimeLimit;
    private int compilationMemoryLimit;

    private File gradingDir;
    private GradingConfig config;
    private GradingLanguage language;
    private GradingSource source;
    private SandboxFactory sandboxFactory;

    private SubtaskAggregator subtaskAggregator;

    private CompilationResult compilationResult;

    private List<List<TestCaseResult>> testGroupResults;
    private List<List<EvaluationResult>> testGroupEvaluationResults;

    private List<SubtaskResult> subtaskResults;
    private SubtaskResult gradingResult;

    private List<List<TestCaseVerdict>> testCaseVerdictsBySubtaskIds;
    private List<List<Integer[]>> testCaseIndicesBySubtaskIds;

    protected BlackBoxGradingEngine() {
        this.compilationTimeLimit = 10000;
        this.compilationMemoryLimit = 1024 * 1024;

        this.subtaskAggregator = new SubtaskAggregator();
    }

    @Override
    public GradingResult grade(File gradingDir, GradingConfig config, GradingLanguage language, GradingSource source, SandboxFactory sandboxFactory)
            throws GradingException {
        this.gradingDir = gradingDir;
        this.config = config;
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

    protected abstract void prepareAlgorithms(GradingConfig config, GradingLanguage language, Map<String, File> sourceFiles, Map<String, File> helperFiles, SandboxFactory sandboxFactory) throws PreparationException;

    protected abstract void cleanUp();

    protected abstract Compiler getCompiler();

    protected abstract Evaluator getEvaluator();

    protected abstract TestCaseAggregator getAggregator();

    private GradingResult tryGrading() throws GradingException {
        verify();
        prepare();
        compile();

        if ((getCompiler() != null) && (!compilationResult.isSuccessful())) {
            return BlackBoxGradingResults.compilationErrorResult(compilationResult.getOutputs());
        }

        evaluate();
        aggregate();

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
            compilationResult = getCompiler().compile(source.getSourceFiles());
        } else {
            GabrielLogger.getLogger().info("No compiler is used.");
        }
        GabrielLogger.getLogger().info("Compilation finished.");
    }

    private void evaluate() throws EvaluationException {
        MDC.put("gradingPhase", "EVALUATE");
        GabrielLogger.getLogger().info("Evaluation started.");

        testGroupResults = Lists.newArrayList();
        testGroupEvaluationResults = Lists.newArrayList();

        testCaseVerdictsBySubtaskIds = Lists.newArrayList();
        testCaseIndicesBySubtaskIds = Lists.newArrayList();

        // +2 because: ids are 1-based, and we can have id=-1 (so we need additional +1 offset)
        for (int i = 0; i < config.getSubtasks().size() + 2; i++) {
            testCaseVerdictsBySubtaskIds.add(Lists.newArrayList());
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
                    evaluationResult = getEvaluator().evaluate(testCaseInput, testCaseOutput);
                }

                TestCaseVerdict testCaseVerdict = evaluationResult.getVerdict();
                if (testCaseVerdict.getVerdict() != Verdict.ACCEPTED && testCaseVerdict.getVerdict() != Verdict.OK) {
                    alreadyFailedSubtaskIds.addAll(testCase.getSubtaskIds());
                    alreadyFailedSubtaskIds.remove(0);
                    alreadyFailedSubtaskIds.remove(-1);
                }

                testGroupResults.get(i).add(new TestCaseResult(toNormalVerdict(testCaseVerdict.getVerdict()), testCaseVerdict.getScore()));
                testGroupEvaluationResults.get(i).add(evaluationResult);

                for (int subtaskId : testCase.getSubtaskIds()) {
                    testCaseVerdictsBySubtaskIds.get(subtaskId + 1).add(testCaseVerdict);
                    testCaseIndicesBySubtaskIds.get(subtaskId + 1).add(new Integer[]{i, j});
                }
            }
        }
        GabrielLogger.getLogger().info("Evaluation & scoring finished.");
    }

    private void aggregate() {
        MDC.put("gradingPhase", "REDUCE");
        GabrielLogger.getLogger().info("Reduction started.");
        ImmutableList.Builder<SubtaskVerdict> subtaskVerdictBuilder = ImmutableList.builder();
        ImmutableList.Builder<SubtaskResult> subtaskResultsBuilder = ImmutableList.builder();
        for (Subtask subtask : config.getSubtasks()) {
            List<TestCaseVerdict> testCaseVerdicts = ImmutableList.copyOf(testCaseVerdictsBySubtaskIds.get(subtask.getId() + 1));
            TestCaseAggregationResult aggregationResult = getAggregator().aggregate(testCaseVerdicts, subtask.getPoints());
            SubtaskVerdict subtaskVerdict = aggregationResult.getSubtaskVerdict();
            List<String> testCasePoints = aggregationResult.getTestCasePoints();

            subtaskVerdictBuilder.add(subtaskVerdict);
            subtaskResultsBuilder.add(new SubtaskResult(toNormalVerdict(subtaskVerdict.getVerdict()), subtaskVerdict.getPoints()));

            for (int i = 0; i < testCaseVerdicts.size(); i++) {
                Integer[] testCaseIndices = testCaseIndicesBySubtaskIds.get(subtask.getId() + 1).get(i);
                int testGroupIndex = testCaseIndices[0];
                int testCaseIndex = testCaseIndices[1];
                TestCaseResult testCaseResult = new TestCaseResult(toNormalVerdict(testCaseVerdicts.get(i).getVerdict()), testCasePoints.get(i));
                testGroupResults.get(testGroupIndex).set(testCaseIndex, testCaseResult);
            }
        }

        subtaskResults = subtaskResultsBuilder.build();
        SubtaskVerdict gradingVerdict = subtaskAggregator.aggregate(subtaskVerdictBuilder.build());
        gradingResult = new SubtaskResult(toNormalVerdict(gradingVerdict.getVerdict()), gradingVerdict.getPoints());
        GabrielLogger.getLogger().info("Aggregation finished.");
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
                judgels.gabriel.api.SandboxExecutionResult newResult = testGroupEvaluationResults.get(i).get(j).getExecutionResult().orElse(null);
                SandboxExecutionResult result = null;
                if (newResult != null) {
                    SandboxExecutionStatus status = SandboxExecutionStatus.valueOf(newResult.getStatus().name());
                    result = new SandboxExecutionResult(status, newResult.getTimeInMilliseconds(), newResult.getMemoryInKilobytes(), newResult.getMessage());
                }
                testCaseFinalResults.add(new TestCaseFinalResult(testGroupResults.get(i).get(j), result, testCase.getSubtaskIds()));
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

        return BlackBoxGradingResults.normalResult(gradingResult, details);
    }

    private NormalVerdict toNormalVerdict(Verdict verdict) {
        switch (verdict) {
            case ACCEPTED: return ScoringVerdict.ACCEPTED;
            case OK: return ScoringVerdict.OK;
            case WRONG_ANSWER: return ScoringVerdict.WRONG_ANSWER;
            case TIME_LIMIT_EXCEEDED: return EvaluationVerdict.TIME_LIMIT_EXCEEDED;
            case SKIPPED: return EvaluationVerdict.SKIPPED;
            default: return EvaluationVerdict.RUNTIME_ERROR;
        }
    }
}
