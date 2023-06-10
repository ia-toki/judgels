package judgels.gabriel.engines;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import judgels.JudgelsObjectMappers;
import judgels.gabriel.aggregators.SubtaskAggregator;
import judgels.gabriel.api.AggregationResult;
import judgels.gabriel.api.Aggregator;
import judgels.gabriel.api.CompilationException;
import judgels.gabriel.api.CompilationResult;
import judgels.gabriel.api.Compiler;
import judgels.gabriel.api.EvaluationException;
import judgels.gabriel.api.EvaluationResult;
import judgels.gabriel.api.Evaluator;
import judgels.gabriel.api.GradingConfig;
import judgels.gabriel.api.GradingEngine;
import judgels.gabriel.api.GradingException;
import judgels.gabriel.api.GradingLanguage;
import judgels.gabriel.api.GradingOptions;
import judgels.gabriel.api.GradingResult;
import judgels.gabriel.api.GradingResultDetails;
import judgels.gabriel.api.GradingSource;
import judgels.gabriel.api.PreparationException;
import judgels.gabriel.api.SandboxFactory;
import judgels.gabriel.api.Subtask;
import judgels.gabriel.api.SubtaskResult;
import judgels.gabriel.api.SubtaskVerdict;
import judgels.gabriel.api.TestCase;
import judgels.gabriel.api.TestCaseResult;
import judgels.gabriel.api.TestCaseVerdict;
import judgels.gabriel.api.TestGroup;
import judgels.gabriel.api.TestGroupResult;
import judgels.gabriel.api.Verdict;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public abstract class BlackboxGradingEngine implements GradingEngine {
    private static final Logger LOGGER = LoggerFactory.getLogger(BlackboxGradingEngine.class);
    private static final ObjectMapper MAPPER = JudgelsObjectMappers.OBJECT_MAPPER;

    private File gradingDir;
    private File compilationDir;
    private File evaluationDir;

    private GradingConfig config;
    private GradingOptions options;
    private GradingLanguage language;
    private GradingSource source;
    private SandboxFactory sandboxFactory;

    private SubtaskAggregator subtaskAggregator;

    private CompilationResult compilationResult;
    private List<List<TestCaseVerdict>> testGroupVerdicts;
    private List<List<String>> testGroupPoints;
    private List<List<EvaluationResult>> testGroupEvaluationResults;
    private List<List<TestCaseVerdict>> testCaseVerdictsBySubtaskIds;
    private List<List<Integer[]>> testCaseIndicesBySubtaskIds;
    private List<SubtaskVerdict> subtaskVerdicts;
    private SubtaskVerdict gradingVerdict;

    public BlackboxGradingEngine() {
        this.subtaskAggregator = new SubtaskAggregator();
    }

    @Override
    public GradingResult grade(
            File gradingDir,
            GradingConfig config,
            GradingOptions options,
            GradingLanguage language,
            GradingSource source,
            SandboxFactory sandboxFactory) throws GradingException {

        this.gradingDir = gradingDir;
        this.config = config;
        this.options = options;
        this.language = language;
        this.source = source;
        this.sandboxFactory = sandboxFactory;

        try {
            return doGrade();
        } finally {
            cleanUp();

            MDC.remove("gradingPhase");
        }
    }

    protected abstract Compiler getCompiler();
    protected abstract Evaluator getEvaluator();
    protected abstract Aggregator getAggregator();
    protected abstract void prepare(
            GradingConfig config,
            GradingOptions options,
            GradingLanguage language,
            Map<String, File> sourceFiles,
            Map<String, File> helperFiles,
            SandboxFactory sandboxFactory,
            File compilationDir,
            File evaluationDir) throws PreparationException;
    protected abstract void cleanUp();

    private GradingResult doGrade() throws GradingException {
        doPrepare();
        if (doCompile()) {
            doEvaluate();
            doAggregate();
            return buildResult();
        } else {
            return buildCompilationErrorResult();
        }
    }

    private void doPrepare() throws PreparationException {
        MDC.put("gradingPhase", "PREPARE");
        LOGGER.info("Preparation started.");
        verifySourceFiles();
        createGradingDirs();
        prepareEngine();
        LOGGER.info("Preparation finished.");
    }

    private void verifySourceFiles() throws PreparationException {
        for (String fieldKey : config.getSourceFileFields().keySet()) {
            if (!source.getSourceFiles().containsKey(fieldKey)) {
                throw new PreparationException("No source file found for '" + fieldKey + "'");
            }
        }
    }

    private void createGradingDirs() throws PreparationException {
        try {
            compilationDir = new File(gradingDir, "compilation");
            FileUtils.forceMkdir(compilationDir);
            evaluationDir = new File(gradingDir, "evaluation");
            FileUtils.forceMkdir(evaluationDir);
        } catch (IOException e) {
            throw new PreparationException(e);
        }
    }

    private void prepareEngine() throws PreparationException {
        prepare(
                config,
                options,
                language,
                source.getSourceFiles(),
                source.getHelperFiles(),
                sandboxFactory,
                compilationDir,
                evaluationDir);
    }

    private boolean doCompile() throws CompilationException {
        MDC.put("gradingPhase", "COMPILE");
        LOGGER.info("Compilation started.");
        getCompiler().compile(source.getSourceFiles());
        compilationResult = getCompiler().compile(source.getSourceFiles());
        LOGGER.info("Compilation finished.");
        return compilationResult.isSuccessful();
    }

    private void doEvaluate() throws EvaluationException {
        MDC.put("gradingPhase", "EVALUATE");
        LOGGER.info("Evaluation started.");

        testGroupVerdicts = Lists.newArrayList();
        testGroupPoints = Lists.newArrayList();
        testGroupEvaluationResults = Lists.newArrayList();

        testCaseVerdictsBySubtaskIds = Lists.newArrayList();
        testCaseIndicesBySubtaskIds = Lists.newArrayList();

        // +2 because: ids are 1-based, and we can have id=-1 (so we need additional +1 offset)
        for (int i = 0; i < config.getSubtasks().size() + 2; i++) {
            testCaseVerdictsBySubtaskIds.add(Lists.newArrayList());
            testCaseIndicesBySubtaskIds.add(Lists.newArrayList());
        }

        for (int i = 0; i < config.getTestData().size(); i++) {
            TestGroup testGroup = config.getTestData().get(i);

            testGroupVerdicts.add(Lists.newArrayList());
            testGroupPoints.add(Lists.newArrayList());
            testGroupEvaluationResults.add(Lists.newArrayList());

            for (int j = 0; j < testGroup.getTestCases().size(); j++) {
                TestCase testCase = testGroup.getTestCases().get(j);

                File testCaseInput = source.getTestDataFiles().get(testCase.getInput());
                File testCaseOutput = source.getTestDataFiles().get(testCase.getOutput());

                EvaluationResult evaluationResult = getEvaluator().evaluate(testCaseInput, testCaseOutput);
                TestCaseVerdict testCaseVerdict = evaluationResult.getVerdict();

                testGroupVerdicts.get(i).add(testCaseVerdict);
                testGroupPoints.get(i).add("");
                testGroupEvaluationResults.get(i).add(evaluationResult);

                for (int subtaskId : testCase.getSubtaskIds()) {
                    testCaseVerdictsBySubtaskIds.get(subtaskId + 1).add(testCaseVerdict);
                    testCaseIndicesBySubtaskIds.get(subtaskId + 1).add(new Integer[]{i, j});
                }
            }
        }
        LOGGER.info("Evaluation finished.");
    }

    private void doAggregate() {
        MDC.put("gradingPhase", "AGGREGATE");
        LOGGER.info("Aggregation started.");
        ImmutableList.Builder<SubtaskVerdict> subtaskVerdictBuilder = ImmutableList.builder();
        for (Subtask subtask : config.getSubtasks()) {
            List<TestCaseVerdict> testCaseVerdicts =
                    ImmutableList.copyOf(testCaseVerdictsBySubtaskIds.get(subtask.getId() + 1));
            AggregationResult aggregationResult =
                    getAggregator().aggregate(testCaseVerdicts, subtask.getPoints());

            SubtaskVerdict subtaskVerdict = aggregationResult.getSubtaskVerdict();
            List<String> testCasePoints = aggregationResult.getTestCasePoints();

            subtaskVerdictBuilder.add(subtaskVerdict);

            for (int i = 0; i < testCaseVerdicts.size(); i++) {
                Integer[] testCaseIndices = testCaseIndicesBySubtaskIds.get(subtask.getId() + 1).get(i);
                int testGroupIndex = testCaseIndices[0];
                int testCaseIndex = testCaseIndices[1];
                String points = testCasePoints.get(i);
                testGroupPoints.get(testGroupIndex).set(testCaseIndex, points);
            }
        }

        subtaskVerdicts = subtaskVerdictBuilder.build();
        gradingVerdict = subtaskAggregator.aggregate(subtaskVerdictBuilder.build());
        LOGGER.info("Aggregation finished.");
    }

    private GradingResult buildCompilationErrorResult() throws GradingException {
        GradingResultDetails details = new GradingResultDetails.Builder()
                .compilationOutputs(compilationResult.getOutputs())
                .build();

        String detailsString;
        try {
            detailsString = MAPPER.writeValueAsString(details);
        } catch (IOException e) {
            throw new GradingException(e);
        }

        return new GradingResult.Builder()
                .verdict(Verdict.COMPILATION_ERROR)
                .score(0)
                .details(detailsString)
                .build();
    }

    private GradingResult buildResult() throws GradingException {
        ImmutableList.Builder<SubtaskResult> subtaskResults = ImmutableList.builder();
        for (int i = 0; i < config.getSubtasks().size(); i++) {
            Subtask subtask = config.getSubtasks().get(i);
            SubtaskVerdict subtaskVerdict = subtaskVerdicts.get(i);
            subtaskResults.add(new SubtaskResult.Builder()
                    .id(subtask.getId())
                    .verdict(subtaskVerdict.getVerdict())
                    .score(subtaskVerdict.getPoints())
                    .build());
        }

        ImmutableList.Builder<TestGroupResult> testGroupResults = ImmutableList.builder();
        for (int i = 0; i < config.getTestData().size(); i++) {
            TestGroup testGroup = config.getTestData().get(i);
            ImmutableList.Builder<TestCaseResult> testCaseResults = ImmutableList.builder();

            for (int j = 0; j < testGroup.getTestCases().size(); j++) {
                TestCase testCase = testGroup.getTestCases().get(j);
                TestCaseVerdict testCaseVerdict = testGroupVerdicts.get(i).get(j);
                String testCaseScore = testGroupPoints.get(i).get(j);
                if (testCaseVerdict.getFeedback().isPresent()) {
                    testCaseScore += testCaseScore.isEmpty() ? "" : " ";
                    testCaseScore += "[" + testCaseVerdict.getFeedback().get() + "]";
                }

                EvaluationResult evaluationResult = testGroupEvaluationResults.get(i).get(j);
                testCaseResults.add(new TestCaseResult.Builder()
                        .verdict(testCaseVerdict.getVerdict())
                        .score(testCaseScore)
                        .executionResult(evaluationResult.getExecutionResult())
                        .subtaskIds(testCase.getSubtaskIds())
                        .revealedInput(evaluationResult.getRevealedInput())
                        .revealedSolutionOutput(evaluationResult.getRevealedSolutionOutput())
                        .build());
            }
            testGroupResults.add(new TestGroupResult.Builder()
                    .id(testGroup.getId())
                    .testCaseResults(testCaseResults.build())
                    .build());
        }

        GradingResultDetails details = new GradingResultDetails.Builder()
                .compilationOutputs(compilationResult.getOutputs())
                .testDataResults(testGroupResults.build())
                .subtaskResults(subtaskResults.build())
                .build();

        String detailsString;
        try {
            detailsString = MAPPER.writeValueAsString(details);
        } catch (IOException e) {
            throw new GradingException(e);
        }

        return new GradingResult.Builder()
                .verdict(gradingVerdict.getVerdict())
                .score((int) Math.round(gradingVerdict.getPoints()))
                .details(detailsString)
                .build();
    }
}
