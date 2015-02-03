package org.iatoki.judgels.gabriel.blackbox;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.iatoki.judgels.gabriel.Grader;
import org.iatoki.judgels.gabriel.GradingException;
import org.iatoki.judgels.gabriel.Language;
import org.iatoki.judgels.gabriel.SandboxFactory;

import java.io.File;
import java.util.List;
import java.util.Map;

public abstract class BlackBoxGrader implements Grader {

    public final BlackBoxGradingResult gradeAfterInitialization(SandboxFactory sandboxFactory, File workingDir, Language language, Map<String, File> sourceFiles, Map<String, File> helperFiles, Map<String, File> testDataFiles, BlackBoxGradingConfig config) throws GradingException {
        prepare(sandboxFactory, workingDir, config, language, sourceFiles, helperFiles);

        CompilationResult compilationResult = getCompiler().compile();
        String compilationOutput = compilationResult.getOutput();

        if (compilationResult.getVerdict() == CompilationVerdict.COMPILATION_ERROR) {
            return BlackBoxGradingResult.compilationErrorResult(compilationOutput);
        }

        List<ImmutableList.Builder<TestCaseResult>> testCaseResultsBySubtaskCollector = Lists.newArrayList();
        for (int i = 0; i < config.getSubtasks().size(); i++) {
            testCaseResultsBySubtaskCollector.add(ImmutableList.builder());
        }

        ImmutableList.Builder<List<TestCaseConcreteResult>> testDataConcreteResults = ImmutableList.builder();
        for (TestGroup testGroup : config.getTestData()) {
            ImmutableList.Builder<TestCaseConcreteResult> testGroupResults = ImmutableList.builder();
            for (TestCase testCase : testGroup.getTestCases()) {
                testGroupResults.add(evaluateAndScore(testCase, testDataFiles, testCaseResultsBySubtaskCollector));
            }
            testDataConcreteResults.add(testGroupResults.build());
        }

        ImmutableList.Builder<SubtaskResult> subtaskResultsBuilder = ImmutableList.builder();
        for (int i = 0; i < config.getSubtasks().size(); i++) {
            Subtask subtask = config.getSubtasks().get(i);
            List<TestCaseResult> testCaseResults = testCaseResultsBySubtaskCollector.get(i).build();
            SubtaskResult subtaskResult = getReducer().reduceTestCases(testCaseResults, subtask);
            subtaskResultsBuilder.add(subtaskResult);
        }

        List<SubtaskResult> subtaskResults = subtaskResultsBuilder.build();
        OverallResult result = getReducer().reduceSubtasks(subtaskResults);

        List<SubtaskConcreteResult> subtaskConcreteResults = Lists.transform(subtaskResults, s -> new SubtaskConcreteResult(s));
        BlackBoxGradingResultDetails details = new BlackBoxGradingResultDetails(compilationOutput, testDataConcreteResults.build(), subtaskConcreteResults);

        return BlackBoxGradingResult.normalResult(result, details);
    }

    protected abstract void prepare(SandboxFactory sandboxFactory, File workingDir, BlackBoxGradingConfig config, Language language, Map<String, File> sourceFiles, Map<String, File> helperFiles) throws PreparationException;

    protected abstract Compiler getCompiler();

    protected abstract Evaluator getEvaluator();

    protected abstract Scorer getScorer();

    protected abstract Reducer getReducer();

    private TestCaseConcreteResult evaluateAndScore(TestCase testCase, Map<String, File> testDataFiles, List<ImmutableList.Builder<TestCaseResult>> testCaseResultsBySubtaskCollector) throws EvaluationException, ScoringException {
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

        return new TestCaseConcreteResult(testCaseResult, evaluationResult.getDetails());
    }
}
