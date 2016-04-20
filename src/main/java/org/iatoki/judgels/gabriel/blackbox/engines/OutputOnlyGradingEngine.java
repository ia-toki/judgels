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
import org.iatoki.judgels.gabriel.blackbox.algorithms.DiffScorer;
import org.iatoki.judgels.gabriel.blackbox.algorithms.OutputOnlyEvaluator;
import org.iatoki.judgels.gabriel.blackbox.algorithms.SimpleReducer;
import org.iatoki.judgels.gabriel.blackbox.configs.BatchGradingConfig;
import org.iatoki.judgels.gabriel.blackbox.configs.OutputOnlyGradingConfig;
import org.iatoki.judgels.gabriel.sandboxes.SandboxFactory;

import java.io.File;
import java.util.Map;

public final class OutputOnlyGradingEngine extends BlackBoxGradingEngine {

    private Evaluator evaluator;
    private Scorer scorer;
    private Reducer reducer;

    @Override
    protected void prepareAlgorithms(BlackBoxGradingConfig config, GradingLanguage language, Map<String, File> sourceFiles, Map<String, File> helperFiles, SandboxFactory sandboxFactory) throws PreparationException {
        String sourceFieldKey = config.getSourceFileFields().keySet().iterator().next();
        File sourceFile = sourceFiles.get(sourceFieldKey);
        OutputOnlyGradingConfig castConfig = (OutputOnlyGradingConfig) config;

        evaluator = new OutputOnlyEvaluator(getEvaluationDir(), sourceFile);

        scorer = new DiffScorer();

        reducer = new SimpleReducer();
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
        return new BatchGradingConfig(getDefaultCompilationTimeLimitInMilliseconds(), getDefaultMemoryLimitInKilobytes(), ImmutableList.of(new TestGroup(0, ImmutableList.of()), new TestGroup(-1, ImmutableList.of())), null);
    }

    @Override
    public GradingConfig createGradingConfigFromJson(String json) {
        return new Gson().fromJson(json, BatchGradingConfig.class);
    }

    @Override
    public void cleanUp() {
    }
}
