package org.iatoki.judgels.gabriel.blackbox.engines;

import com.google.common.collect.ImmutableList;
import judgels.gabriel.aggregators.SumAggregator;
import judgels.gabriel.api.Compiler;
import judgels.gabriel.api.Evaluator;
import judgels.gabriel.api.GradingLanguage;
import judgels.gabriel.api.PreparationException;
import judgels.gabriel.api.Sandbox;
import judgels.gabriel.api.SandboxFactory;
import judgels.gabriel.api.Scorer;
import judgels.gabriel.api.TestCaseAggregator;
import judgels.gabriel.api.GradingConfig;
import judgels.gabriel.api.TestGroup;
import judgels.gabriel.engines.outputonly.OutputOnlyGradingConfig;
import judgels.gabriel.evaluators.OutputOnlyEvaluator;
import judgels.gabriel.evaluators.helpers.CustomScorer;
import judgels.gabriel.evaluators.helpers.DiffScorer;
import judgels.gabriel.languages.cpp.Cpp11GradingLanguage;
import judgels.gabriel.languages.cpp.CppFamilyGradingLanguage;
import org.iatoki.judgels.gabriel.blackbox.BlackBoxGradingEngine;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public final class OutputOnlyGradingEngine extends BlackBoxGradingEngine {
    private final OutputOnlyEvaluator evaluator;
    private final SumAggregator aggregator;

    private Sandbox scorerSandbox;

    private CppFamilyGradingLanguage scorerLanguage;

    public OutputOnlyGradingEngine() {
        this.evaluator = new OutputOnlyEvaluator();
        this.aggregator = new SumAggregator();
        this.scorerLanguage = new Cpp11GradingLanguage();
    }

    @Override
    protected void prepareAlgorithms(GradingConfig config, GradingLanguage language, Map<String, File> sourceFiles, Map<String, File> helperFiles, SandboxFactory sandboxFactory) throws PreparationException {
        String sourceFieldKey = config.getSourceFileFields().keySet().iterator().next();
        File sourceFile = sourceFiles.get(sourceFieldKey);
        OutputOnlyGradingConfig castConfig = (OutputOnlyGradingConfig) config;

        Scorer scorer;
        if (castConfig.getCustomScorer().isPresent()) {
            scorerSandbox = sandboxFactory.newSandbox();
            File scorerFile = helperFiles.get(castConfig.getCustomScorer().get());
            CustomScorer customScorer = new CustomScorer();
            customScorer.prepare(scorerSandbox, getScoringDir(), scorerLanguage, scorerFile);
            scorer = customScorer;
        } else {
            scorer = new DiffScorer();
        }

        evaluator.prepare(scorer, getEvaluationDir(), sourceFile);
    }

    public void setScorerLanguage(CppFamilyGradingLanguage scorerLanguage) {
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
    protected TestCaseAggregator getAggregator() {
        return aggregator;
    }

    @Override
    public String getName() {
        return "Output Only";
    }

    @Override
    public GradingConfig createDefaultGradingConfig() {
        return new OutputOnlyGradingConfig.Builder()
                .addTestData(TestGroup.of(0, ImmutableList.of()))
                .addTestData(TestGroup.of(-1, ImmutableList.of()))
                .build();
    }

    @Override
    public GradingConfig createGradingConfigFromJson(String json) throws IOException {
        return MAPPER.readValue(json, OutputOnlyGradingConfig.class);
    }

    @Override
    public void cleanUp() {
        if (scorerSandbox != null) {
            scorerSandbox.cleanUp();
        }
    }
}
