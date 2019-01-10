package org.iatoki.judgels.gabriel.blackbox.engines;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import org.iatoki.judgels.gabriel.GradingConfig;
import org.iatoki.judgels.gabriel.GradingLanguage;
import org.iatoki.judgels.gabriel.blackbox.BlackBoxGradingConfig;
import org.iatoki.judgels.gabriel.blackbox.BlackBoxGradingEngine;
import org.iatoki.judgels.gabriel.blackbox.Compiler;
import org.iatoki.judgels.gabriel.blackbox.Evaluator;
import org.iatoki.judgels.gabriel.blackbox.PreparationException;
import org.iatoki.judgels.gabriel.blackbox.Reducer;
import org.iatoki.judgels.gabriel.blackbox.Scorer;
import org.iatoki.judgels.gabriel.blackbox.TestGroup;
import org.iatoki.judgels.gabriel.blackbox.algorithms.CustomScorer;
import org.iatoki.judgels.gabriel.blackbox.algorithms.DiffScorer;
import org.iatoki.judgels.gabriel.blackbox.algorithms.OutputOnlyEvaluator;
import org.iatoki.judgels.gabriel.blackbox.algorithms.SimpleReducer;
import org.iatoki.judgels.gabriel.blackbox.configs.OutputOnlyGradingConfig;
import org.iatoki.judgels.gabriel.blackbox.languages.Cpp11GradingLanguage;
import org.iatoki.judgels.gabriel.sandboxes.Sandbox;
import org.iatoki.judgels.gabriel.sandboxes.SandboxFactory;

import java.io.File;
import java.util.Map;

public final class OutputOnlyGradingEngine extends BlackBoxGradingEngine {

    private Evaluator evaluator;
    private Scorer scorer;
    private Reducer reducer;

    private Sandbox scorerSandbox;

    private int scoringTimeLimit;
    private int scoringMemoryLimit;
    private GradingLanguage scorerLanguage;

    public OutputOnlyGradingEngine() {
        this.scoringTimeLimit = 10000;
        this.scoringMemoryLimit = 1024 * 1024;
        this.scorerLanguage = new Cpp11GradingLanguage();
    }

    @Override
    protected void prepareAlgorithms(BlackBoxGradingConfig config, GradingLanguage language, Map<String, File> sourceFiles, Map<String, File> helperFiles, SandboxFactory sandboxFactory) throws PreparationException {
        String sourceFieldKey = config.getSourceFileFields().keySet().iterator().next();
        File sourceFile = sourceFiles.get(sourceFieldKey);
        OutputOnlyGradingConfig castConfig = (OutputOnlyGradingConfig) config;

        evaluator = new OutputOnlyEvaluator(getEvaluationDir(), sourceFile);

        if (castConfig.getCustomScorer() != null) {
            scorerSandbox = sandboxFactory.newSandbox();
            File scorerFile = helperFiles.get(castConfig.getCustomScorer());
            scorer = new CustomScorer(scorerSandbox, getScoringDir(), scorerLanguage, scorerFile, getCompilationTimeLimitInMilliseconds(), getCompilationMemoryLimitInKilobytes(), scoringTimeLimit, scoringMemoryLimit);
        } else {
            scorer = new DiffScorer();
        }

        reducer = new SimpleReducer();
    }

    public void setScorerLanguage(GradingLanguage scorerLanguage) {
        this.scorerLanguage = scorerLanguage;
    }

    @Override
    protected Compiler getCompiler() {
        return null;
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
    public String getName() {
        return "Output Only";
    }

    @Override
    public GradingConfig createDefaultGradingConfig() {
        return new OutputOnlyGradingConfig(ImmutableList.of(new TestGroup(0, ImmutableList.of()), new TestGroup(-1, ImmutableList.of())), null);
    }

    @Override
    public GradingConfig createGradingConfigFromJson(String json) {
        return new Gson().fromJson(json, OutputOnlyGradingConfig.class);
    }

    @Override
    public void cleanUp() {
        if (scorerSandbox != null) {
            scorerSandbox.cleanUp();
        }
    }
}
