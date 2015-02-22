package org.iatoki.judgels.gabriel.engines;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import org.iatoki.judgels.gabriel.GradingConfig;
import org.iatoki.judgels.gabriel.GradingLanguage;
import org.iatoki.judgels.gabriel.blackbox.Sandbox;
import org.iatoki.judgels.gabriel.blackbox.SandboxFactory;
import org.iatoki.judgels.gabriel.blackbox.BlackBoxGradingEngine;
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
import org.iatoki.judgels.gabriel.blackbox.algorithms.CustomScorer;
import org.iatoki.judgels.gabriel.blackbox.algorithms.DiffScorer;
import org.iatoki.judgels.gabriel.blackbox.configs.BatchWithSubtasksGradingConfig;
import org.iatoki.judgels.gabriel.languages.Cpp11GradingLanguage;

import java.io.File;
import java.util.Map;

public final class BatchWithSubtasksGradingEngine extends BlackBoxGradingEngine {
    private Compiler compiler;
    private Evaluator evaluator;
    private Scorer scorer;
    private Reducer reducer;

    private Sandbox compilerSandbox;
    private Sandbox evaluatorSandbox;
    private Sandbox scorerSandbox;

    private int scoringTimeLimit;
    private int scoringMemoryLimit;
    private GradingLanguage scorerLanguage;

    public BatchWithSubtasksGradingEngine() {
        this.scoringMemoryLimit = 10000;
        this.scoringMemoryLimit = 1024 * 1024;
        this.scorerLanguage = new Cpp11GradingLanguage();
    }

    @Override
    public String getName() {
        return "Batch with Subtasks";
    }

    @Override
    public GradingConfig createDefaultGradingConfig() {
        return new BatchWithSubtasksGradingConfig(getDefaultCompilationTimeLimitInMilliseconds(), getCompilationMemoryLimitInKilobytes(), ImmutableList.of(new TestGroup(0, ImmutableList.of())), ImmutableList.of(), null);
    }

    @Override
    public GradingConfig createGradingConfigFromJson(String json) {
        return new Gson().fromJson(json, BatchWithSubtasksGradingConfig.class);
    }

    @Override
    protected void prepare(SandboxFactory sandboxFactory, File workingDir, BlackBoxGradingConfig config, GradingLanguage language, Map<String, File> sourceFiles, Map<String, File> helperFiles) throws PreparationException {
        String sourceFieldKey = config.getSourceFileFields().keySet().iterator().next();
        File sourceFile = sourceFiles.get(sourceFieldKey);
        BatchWithSubtasksGradingConfig castConfig = (BatchWithSubtasksGradingConfig) config;

        prepareWorkingDirs(workingDir);

        compilerSandbox = sandboxFactory.newSandbox();
        compiler = new SingleSourceFileCompiler(compilerSandbox, getCompilationDir(), language, sourceFieldKey, sourceFile, getCompilationTimeLimitInMilliseconds(), getCompilationMemoryLimitInKilobytes());

        evaluatorSandbox = sandboxFactory.newSandbox();
        evaluator = new BatchEvaluator(evaluatorSandbox, getCompilationDir(), getEvaluationDir(), language, sourceFile, castConfig.getTimeLimitInMilliseconds(), castConfig.getMemoryLimitInKilobytes());

        if (castConfig.getCustomScorer() != null) {
            scorerSandbox = sandboxFactory.newSandbox();
            File scorerFile = helperFiles.get(castConfig.getCustomScorer());
            scorer = new CustomScorer(scorerSandbox, getEvaluationDir(), getScoringDir(), scorerLanguage, scorerFile, getCompilationTimeLimitInMilliseconds(), getCompilationMemoryLimitInKilobytes(), scoringTimeLimit, scoringMemoryLimit);
        } else {
            scorer = new DiffScorer(getEvaluationDir());
        }

        reducer = new SubtaskReducer();
    }

    public void setScoringTimeLimitInMilliseconds(int scoringTimeLimit) {
        this.scoringTimeLimit = scoringTimeLimit;
    }

    public void setScoringMemoryLimitInKilobytes(int scoringMemoryLimit) {
        this.scoringMemoryLimit = scoringMemoryLimit;
    }

    public void setScorerLanguage(GradingLanguage scorerLanguage) {
        this.scorerLanguage = scorerLanguage;
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
        if (compilerSandbox != null) {
            compilerSandbox.cleanUp();
        }
        if (evaluatorSandbox != null) {
            evaluatorSandbox.cleanUp();
        }
        if (scorerSandbox != null) {
            scorerSandbox.cleanUp();
        }
    }
}
