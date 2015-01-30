package org.iatoki.judgels.gabriel.blackbox;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import org.iatoki.judgels.gabriel.Grader;
import org.iatoki.judgels.gabriel.GradingException;
import org.iatoki.judgels.gabriel.Language;
import org.iatoki.judgels.gabriel.SandboxFactory;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

        ImmutableList.Builder<TestCaseDetailedResult> sampleTestDataResults = ImmutableList.builder();
        for (SampleTestCase testCase : config.getSampleTestData()) {
            File testCaseInput = testDataFiles.get(testCase.getInput());
            File testCaseOutput = testDataFiles.get(testCase.getOutput());
            sampleTestDataResults.add(evaluateAndScore(testCaseInput, testCaseOutput, testCase.getSubtaskNumbers(), testCaseResultsBySubtaskCollector));
        }

        ImmutableList.Builder<List<TestCaseDetailedResult>> testDataResults = ImmutableList.builder();
        for (TestGroup testGroup : config.getTestData()) {
            ImmutableList.Builder<TestCaseDetailedResult> testGroupResults = ImmutableList.builder();
            for (TestCase testCase : testGroup.getTestCases()) {
                File testCaseInput = testDataFiles.get(testCase.getInput());
                File testCaseOutput = testDataFiles.get(testCase.getOutput());
                testGroupResults.add(evaluateAndScore(testCaseInput, testCaseOutput, testGroup.getSubtaskNumbers(), testCaseResultsBySubtaskCollector));
            }
            testDataResults.add(testGroupResults.build());
        }

        ImmutableList.Builder<SubtaskResult> subtaskResults = ImmutableList.builder();
        for (int i = 0; i < config.getSubtasks().size(); i++) {
            Subtask subtask = config.getSubtasks().get(i);
            List<TestCaseResult> testCaseResults = testCaseResultsBySubtaskCollector.get(i).build();
            SubtaskResult subtaskResult = getReducer().reduceTestCases(testCaseResults, subtask);
            subtaskResults.add(subtaskResult);
        }

        OverallResult result = getReducer().reduceSubtasks(subtaskResults.build());
        BlackBoxGradingResultDetails details = new BlackBoxGradingResultDetails(compilationOutput, sampleTestDataResults.build(), testDataResults.build(), subtaskResults.build());

        return BlackBoxGradingResult.normalResult(result, details);
    }

    protected abstract void prepare(SandboxFactory sandboxFactory, File workingDir, BlackBoxGradingConfig config, Language language, Map<String, File> sourceFiles, Map<String, File> helperFiles) throws PreparationException;

    protected abstract Compiler getCompiler();

    protected abstract Evaluator getEvaluator();

    protected abstract Scorer getScorer();

    protected abstract Reducer getReducer();

    private TestCaseDetailedResult evaluateAndScore(File testCaseInput, File testCaseOutput, Set<Integer> subtaskNumbers, List<ImmutableList.Builder<TestCaseResult>> testCaseResultsBySubtaskCollector) throws EvaluationException, ScoringException {
        EvaluationResult evaluationResult = getEvaluator().evaluate(testCaseInput, subtaskNumbers);

        TestCaseResult testCaseResult;
        if (evaluationResult.getVerdict() == EvaluationVerdict.OK) {
            ScoringResult scoringResult = getScorer().score(testCaseInput, testCaseOutput);
            testCaseResult = TestCaseResult.fromScoringResult(scoringResult);
        } else {
            testCaseResult = TestCaseResult.fromEvaluationResult(evaluationResult);
        }

        for (int subtaskNumber : subtaskNumbers) {
            testCaseResultsBySubtaskCollector.get(subtaskNumber).add(testCaseResult);
        }

        return new TestCaseDetailedResult(testCaseResult, evaluationResult.getDetails());
    }
}
