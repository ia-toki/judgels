package org.iatoki.judgels.gabriel.blackbox;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.iatoki.judgels.gabriel.Grader;
import org.iatoki.judgels.gabriel.GradingException;
import org.iatoki.judgels.gabriel.Language;
import org.iatoki.judgels.gabriel.SandboxProvider;
import org.iatoki.judgels.gabriel.Verdict;

import java.io.File;
import java.util.List;
import java.util.Map;

public abstract class BlackBoxGrader implements Grader {

    public final BlackBoxGradingResult grade(SandboxProvider sandboxProvider, File tempDir, Language language, Map<String, File> sourceFiles, Map<String, File> helperFiles, Map<String, File> testDataFiles, BlackBoxGradingConfig config) throws GradingException {
        prepare(sandboxProvider, tempDir, config, language, sourceFiles, helperFiles);

        CompilationResult compilationResult = getCompiler().compile();

        List<List<ScoringResult>> scoringResultsBySubtask = Lists.newArrayList();
        for (Subtask subtask : config.getSubtasks()) {
            scoringResultsBySubtask.add(Lists.newArrayList());
        }

        List<List<ScoringResult>> sampleTestDataResults = evaluateAndScore(compilationResult.getExecutableFiles(), testDataFiles, config.getSampleTestData(), scoringResultsBySubtask);
        List<List<ScoringResult>> testDataResults = evaluateAndScore(compilationResult.getExecutableFiles(), testDataFiles, config.getTestData(), scoringResultsBySubtask);

        List<SubtaskResult> subtaskResults = reduceTestCaseResults(scoringResultsBySubtask, config.getSubtasks());
        int overallScore = reduceToOverallScore(subtaskResults);
        Verdict overallVerdict = reduceToOverallVerdict(subtaskResults);

        BlackBoxGradingResultDetails details = new BlackBoxGradingResultDetails(compilationResult.getOutput(), sampleTestDataResults, testDataResults, subtaskResults);

        return BlackBoxGradingResult.ok(overallVerdict, overallScore, details);
    }

    public abstract BlackBoxGradingConfig parseGradingConfigFromJson(String json);

    protected abstract void prepare(SandboxProvider provider, File tempDir, BlackBoxGradingConfig config, Language language, Map<String, File> sourceFiles, Map<String, File> helperFiles) throws PreparationException;

    protected abstract Compiler getCompiler();

    protected abstract Evaluator getEvaluator();

    protected abstract Scorer getScorer();

    protected abstract Reducer getReducer();

    private List<List<ScoringResult>> evaluateAndScore(Map<String, File> executableFiles, Map<String, File> testDataFiles, List<TestSet> testSets, List<List<ScoringResult>> scoringResultsBySubtask) throws EvaluationException, ScoringException {
        ImmutableList.Builder<List<ScoringResult>> testSetResults = ImmutableList.builder();

        for (TestSet testSet : testSets) {
            ImmutableList.Builder<ScoringResult> testCaseResults = ImmutableList.builder();

            for (TestCase testCase : testSet.getTestCases()) {
                File testCaseInputFile = testDataFiles.get(testCase.getInput());
                File testCaseOutputFile = testDataFiles.get(testCase.getOutput());
                EvaluationResult evaluationResult = getEvaluator().evaluate(executableFiles, testCaseInputFile);

                Verdict evaluationVerdict = evaluationResult.getExecutionResult().getVerdict();
                ScoringResult scoringResult;
                if (evaluationVerdict == Verdict.OK) {
                    scoringResult = getScorer().score(evaluationResult.getEvaluationOutputFiles(), testCaseInputFile, testCaseOutputFile);
                } else {
                    scoringResult = new ScoringResult(evaluationVerdict, "");
                }

                for (int subtaskNumber : testSet.getSubtaskNumbers()) {
                    scoringResultsBySubtask.get(subtaskNumber).add(scoringResult);
                }
                testCaseResults.add(new ScoringResult(evaluationVerdict, ""));
            }

            testSetResults.add(testCaseResults.build());
        }

        return testSetResults.build();
    }

    private List<SubtaskResult> reduceTestCaseResults(List<List<ScoringResult>> scoringResultsBySubtask, List<Subtask> subtasks) throws ReductionException {

        ImmutableList.Builder<SubtaskResult> subtaskResults = ImmutableList.builder();
        for (int i = 0; i < subtasks.size(); i++) {
            ImmutableSet.Builder<Verdict> testCaseVerdicts = ImmutableSet.builder();
            ImmutableList.Builder<String> testCaseScores = ImmutableList.builder();
            for (ScoringResult scoringResult : scoringResultsBySubtask.get(i)) {
                testCaseScores.add(scoringResult.getScore());
                testCaseVerdicts.add(scoringResult.getVerdict());
            }

            Subtask subtask = subtasks.get(i);
            Verdict verdict = getReducer().reduceVerdicts(testCaseVerdicts.build());
            double score = getReducer().reduceTestCaseScores(testCaseScores.build(), subtask.getPoints(), subtask.getParam());

            subtaskResults.add(new SubtaskResult(verdict, score));
        }

        return subtaskResults.build();
    }

    private int reduceToOverallScore(List<SubtaskResult> subtaskResults) throws ReductionException {
        return getReducer().reduceSubtaskScores(Lists.transform(subtaskResults, e -> e.getScore()));
    }

    private Verdict reduceToOverallVerdict(List<SubtaskResult> subtaskResults) throws ReductionException {
        return getReducer().reduceVerdicts(Sets.newHashSet(Lists.transform(subtaskResults, e -> e.getVerdict())));
    }
}
