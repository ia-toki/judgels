package org.iatoki.judgels.gabriel.blackbox;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.iatoki.judgels.gabriel.GradingEngine;
import org.iatoki.judgels.gabriel.GradingException;
import org.iatoki.judgels.gabriel.GradingLanguage;

import java.io.File;
import java.util.List;
import java.util.Map;

public abstract class BlackBoxGradingEngine implements GradingEngine {

    public final BlackBoxGradingResult gradeAfterInitialization(SandboxFactory sandboxFactory, File workingDir, GradingLanguage language, Map<String, File> sourceFiles, Map<String, File> helperFiles, Map<String, File> testDataFiles, BlackBoxGradingConfig config) throws GradingException {
        verifySourceFiles(sourceFiles, config);

        prepare(sandboxFactory, workingDir, config, language, sourceFiles, helperFiles);

        CompilationResult compilationResult = getCompiler().compile();
        Map<String, String> compilationOutput = compilationResult.getOutputs();

        if (compilationResult.getVerdict() == CompilationVerdict.COMPILATION_ERROR) {
            return BlackBoxGradingResult.compilationErrorResult(compilationOutput);
        }

        List<ImmutableList.Builder<TestCaseResult>> testCaseResultsBySubtaskCollector = Lists.newArrayList();
        for (int i = 0; i < config.getSubtasks().size(); i++) {
            testCaseResultsBySubtaskCollector.add(ImmutableList.builder());
        }

        ImmutableList.Builder<TestGroupFinalResult> testGroupFinalResults = ImmutableList.builder();
        for (TestGroup testGroup : config.getTestData()) {
            ImmutableList.Builder<TestCaseFinalResult> testCaseFinalResults = ImmutableList.builder();
            for (TestCase testCase : testGroup.getTestCases()) {
                testCaseFinalResults.add(evaluateAndScore(testCase, testDataFiles, testCaseResultsBySubtaskCollector));
            }
            testGroupFinalResults.add(new TestGroupFinalResult(testGroup.getId(), testCaseFinalResults.build()));
        }

        ImmutableList.Builder<SubtaskResult> subtaskResultsBuilder = ImmutableList.builder();
        for (int i = 0; i < config.getSubtasks().size(); i++) {
            Subtask subtask = config.getSubtasks().get(i);
            List<TestCaseResult> testCaseResults = testCaseResultsBySubtaskCollector.get(i).build();
            SubtaskResult subtaskResult = getReducer().reduceTestCases(testCaseResults, subtask);
            subtaskResultsBuilder.add(subtaskResult);
        }

        List<SubtaskResult> subtaskResults = subtaskResultsBuilder.build();
        ReductionResult result = getReducer().reduceSubtasks(subtaskResults);

        ImmutableList.Builder<SubtaskFinalResult> subtaskFinalResults = ImmutableList.builder();
        for (int i = 0; i < config.getSubtasks().size(); i++) {
            subtaskFinalResults.add(new SubtaskFinalResult(config.getSubtasks().get(i).getId(), subtaskResults.get(i)));
        }

        BlackBoxGradingResultDetails details = new BlackBoxGradingResultDetails(compilationOutput, testGroupFinalResults.build(), subtaskFinalResults.build());

        return BlackBoxGradingResult.normalResult(result, details);
    }

    protected abstract void prepare(SandboxFactory sandboxFactory, File workingDir, BlackBoxGradingConfig config, GradingLanguage language, Map<String, File> sourceFiles, Map<String, File> helperFiles) throws PreparationException;

    protected abstract Compiler getCompiler();

    protected abstract Evaluator getEvaluator();

    protected abstract Scorer getScorer();

    protected abstract Reducer getReducer();

    private TestCaseFinalResult evaluateAndScore(TestCase testCase, Map<String, File> testDataFiles, List<ImmutableList.Builder<TestCaseResult>> testCaseResultsBySubtaskCollector) throws EvaluationException, ScoringException {
        File testCaseInput = testDataFiles.get(testCase.getInput());
        File testCaseOutput = testDataFiles.get(testCase.getOutput());
        EvaluationResult evaluationResult = getEvaluator().evaluate(testCaseInput, testCase.getSubtaskIds());

        TestCaseResult testCaseResult;
        if (evaluationResult.getVerdict() == EvaluationVerdict.OK) {
            ScoringResult scoringResult = getScorer().score(testCaseInput, testCaseOutput);
            testCaseResult = TestCaseResult.fromScoringResult(scoringResult);
        } else {
            testCaseResult = TestCaseResult.fromEvaluationResult(evaluationResult);
        }

        for (int subtaskId : testCase.getSubtaskIds()) {
            if (subtaskId > 0) {
                testCaseResultsBySubtaskCollector.get(subtaskId - 1).add(testCaseResult);
            }
        }

        return new TestCaseFinalResult(testCaseResult, evaluationResult.getExecutionResult(), testCase.getSubtaskIds());
    }


    private void verifySourceFiles(Map<String, File> sourceFiles, BlackBoxGradingConfig config) throws PreparationException {
        for (String fieldKey : config.getSourceFileFields().keySet()) {
            if (!sourceFiles.containsKey(fieldKey)) {
                throw new PreparationException("No source file found for '" + fieldKey + "'");
            }
        }
    }
}
