package org.iatoki.judgels.gabriel.graders;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.iatoki.judgels.gabriel.GradingConfig;
import org.iatoki.judgels.gabriel.GradingLanguage;
import org.iatoki.judgels.gabriel.Language;
import org.iatoki.judgels.gabriel.LanguageRegistry;
import org.iatoki.judgels.gabriel.Sandbox;
import org.iatoki.judgels.gabriel.SandboxFactory;
import org.iatoki.judgels.gabriel.blackbox.BlackBoxGrader;
import org.iatoki.judgels.gabriel.blackbox.BlackBoxGradingConfig;
import org.iatoki.judgels.gabriel.blackbox.Compiler;
import org.iatoki.judgels.gabriel.blackbox.Evaluator;
import org.iatoki.judgels.gabriel.blackbox.PreparationException;
import org.iatoki.judgels.gabriel.blackbox.Reducer;
import org.iatoki.judgels.gabriel.blackbox.Scorer;
import org.iatoki.judgels.gabriel.blackbox.TestGroup;
import org.iatoki.judgels.gabriel.blackbox.algorithms.SingleSourceFileCompiler;
import org.iatoki.judgels.gabriel.blackbox.algorithms.BatchEvaluator;
import org.iatoki.judgels.gabriel.blackbox.algorithms.SubtaskReducer;
import org.iatoki.judgels.gabriel.blackbox.algorithms.SubtaskCustomScorer;
import org.iatoki.judgels.gabriel.blackbox.algorithms.SubtaskScorer;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public final class BatchWithSubtasksGrader extends BlackBoxGrader {
    private Compiler compiler;
    private Evaluator evaluator;
    private Scorer scorer;
    private Reducer reducer;

    private Sandbox compilerSandbox;
    private Sandbox evaluatorSandbox;
    private Sandbox scorerSandbox;

    @Override
    public String getName() {
        return "Batch with Subtasks";
    }

    @Override
    public GradingConfig createDefaultGradingConfig() {
        BatchWithSubtasksGradingConfig config = new BatchWithSubtasksGradingConfig();
        config.timeLimitInMilliseconds = 2000;
        config.memoryLimitInKilobytes = 65536;
        config.testData = ImmutableList.of(new TestGroup(0, ImmutableList.of()));
        config.subtaskPoints = ImmutableList.of();

        return config;
    }

    @Override
    public GradingConfig createGradingConfigFromJson(String json) {
        return new Gson().fromJson(json, BatchWithSubtasksGradingConfig.class);
    }

    @Override
    protected void prepare(SandboxFactory sandboxFactory, File workingDir, BlackBoxGradingConfig config, Language language, Map<String, File> sourceFiles, Map<String, File> helperFiles) throws PreparationException {
        File sourceFile = sourceFiles.get("source");
        BatchWithSubtasksGradingConfig thisConfig = (BatchWithSubtasksGradingConfig) config;

        File compilationDir;
        File evaluationDir;
        File scoringDir;

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

        compilerSandbox = sandboxFactory.newSandbox();
        compiler = new SingleSourceFileCompiler(compilerSandbox, compilationDir, language, sourceFile, 10000, 100 * 1024);

        evaluatorSandbox = sandboxFactory.newSandbox();
        evaluator = new BatchEvaluator(evaluatorSandbox, compilationDir, evaluationDir, language, sourceFile, thisConfig.getTimeLimitInMilliseconds(), thisConfig.getMemoryLimitInKilobytes());

        if (thisConfig.getCustomScorer() != null) {
            scorerSandbox = sandboxFactory.newSandbox();
            Language cppLanguage = LanguageRegistry.getInstance().getLanguage(GradingLanguage.CPP);
            File scorerFile = helperFiles.get(thisConfig.getCustomScorer());
            scorer = new SubtaskCustomScorer(scorerSandbox, evaluationDir, scoringDir, cppLanguage, scorerFile, 10000, 100 * 1024, 10000, 100 * 1024);
        } else {
            scorer = new SubtaskScorer(evaluationDir);
        }

        reducer = new SubtaskReducer();
    }

    @Override
    protected Compiler getCompiler() {
        return compiler;
    }

    @Override
    protected Evaluator getEvaluator() {
        return evaluator;
    }

    @Override
    protected Scorer getScorer() {
        return scorer;
    }

    @Override
    protected Reducer getReducer() {
        return reducer;
    }

    @Override
    public void cleanUp() {
        compilerSandbox.cleanUp();
        evaluatorSandbox.cleanUp();

        if (scorerSandbox != null) {
            scorerSandbox.cleanUp();
        }
    }
}
