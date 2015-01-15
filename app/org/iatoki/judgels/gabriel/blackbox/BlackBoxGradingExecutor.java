package org.iatoki.judgels.gabriel.blackbox;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.iatoki.judgels.gabriel.ExecutionStatus;
import org.iatoki.judgels.gabriel.ExecutionVerdict;
import org.iatoki.judgels.gabriel.GradingExecutor;
import org.iatoki.judgels.gabriel.Language;
import org.iatoki.judgels.gabriel.Sandbox;
import org.iatoki.judgels.gabriel.SandboxProvider;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class BlackBoxGradingExecutor implements GradingExecutor {

    protected abstract CompilationExecutor getCompilationExecutor();

    protected abstract EvaluationExecutor getEvaluationExecutor();

    protected abstract ScoringExecutor getScoringExecutor();

    protected abstract ScoreReducer getScoreReducer();

    public final GradingVerdict grade(SandboxProvider sandboxProvider, Language language, File sourceDirectory, File helperDirectory, File testDataDirectory, GradingConfig config) {

        Map<String, File> sourceFiles = listFilesAsMap(sourceDirectory);
        Map<String, File> helperFiles = listFilesAsMap(helperDirectory);
        Map<String, File> testDataFiles = listFilesAsMap(testDataDirectory);

        GradingContext context = new GradingContext(language, sourceFiles, helperFiles);

        Sandbox compilationSandbox = sandboxProvider.newSandbox();
        CompilationVerdict compilationVerdict = getCompilationExecutor().compile(compilationSandbox, context);

        if (compilationVerdict.getExecutionVerdict().getStatus() != ExecutionStatus.OK) {
            return createCompileErrorVerdict();
        }

        ImmutableList.Builder<TestSetVerdict> testSetVerdicts = ImmutableList.builder();

        List<Set<ScoringVerdict>> scoringVerdictsBySubtask = Lists.newArrayList();

        for (int i = 0; i < 10; i++) {
            scoringVerdictsBySubtask.add(Sets.<ScoringVerdict> newHashSet());
        }

        for (TestSet testSet : config.getTestData()) {
            ImmutableList.Builder<TestCaseVerdict> testCaseVerdicts = ImmutableList.builder();
            for (TestCase testCase : testSet.getTestCases()) {
                Sandbox evaluationSandbox = sandboxProvider.newSandbox();
                for (String filename : compilationVerdict.getNeededOutputFiles()) {
                    evaluationSandbox.addFile(compilationSandbox.getFile(filename));
                }

                EvaluationVerdict evaluationVerdict = getEvaluationExecutor().evaluate(evaluationSandbox, context, testDataFiles.get(testCase.getInput()));
                ScoringVerdict scoringVerdict;

                if (evaluationVerdict.getExecutionVerdict().getStatus() != ExecutionStatus.OK) {

                    Sandbox scoringSandbox = sandboxProvider.newSandbox();
                    for (String filename : evaluationVerdict.getNeededOutputFiles()) {
                        scoringSandbox.addFile(evaluationSandbox.getFile(filename));
                    }

                    scoringVerdict = getScoringExecutor().score(scoringSandbox, context, testDataFiles.get(testCase.getInput()), testDataFiles.get(testCase.getOutput()));
                } else {
                    scoringVerdict = new ScoringVerdict(null, ScoringStatus.EVALUATION_ERROR, "");
                }

                testCaseVerdicts.add(new TestCaseVerdict(evaluationVerdict, scoringVerdict));

                for (int subtask : testSet.getSubtasks()) {
                    scoringVerdictsBySubtask.get(subtask).add(scoringVerdict);
                }
            }
            testSetVerdicts.add(new TestSetVerdict(testCaseVerdicts.build()));
        }

        List<Double> subtaskScores = Lists.newArrayList();
        for (int i = 0; i < 10; i++) {
            Subtask subtask = config.getSubtasks().get(i);
            subtaskScores.add(getScoreReducer().reduce(scoringVerdictsBySubtask.get(i), subtask.getPoints(), subtask.getParam()));
        }

        OverallVerdict overallVerdict = OverallVerdict.OK;
        double overallScore = subtaskScores.stream().mapToDouble(e -> e).sum();
        GradingVerdictDetails details = new GradingVerdictDetails(compilationVerdict.getExecutionVerdict(), testSetVerdicts.build(), subtaskScores);

        return new GradingVerdict(overallVerdict, overallScore, details);
    }

    private Map<String, File> listFilesAsMap(File directory) {
        return Arrays.asList(directory.listFiles()).stream().collect(Collectors.toMap(e -> e.getName(), e -> e));
    }

    private GradingVerdict createCompileErrorVerdict() {
        return new GradingVerdict(OverallVerdict.COMPILE_ERROR, 0, null);
    }
}
