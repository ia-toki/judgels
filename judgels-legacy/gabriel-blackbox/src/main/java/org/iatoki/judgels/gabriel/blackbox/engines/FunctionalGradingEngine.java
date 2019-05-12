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
import judgels.gabriel.compilers.FunctionalCompiler;
import judgels.gabriel.engines.functional.FunctionalGradingConfig;
import judgels.gabriel.evaluators.FunctionalEvaluator;
import judgels.gabriel.evaluators.helpers.CustomScorer;
import judgels.gabriel.evaluators.helpers.DiffScorer;
import judgels.gabriel.languages.cpp.Cpp11GradingLanguage;
import judgels.gabriel.languages.cpp.CppFamilyGradingLanguage;
import org.iatoki.judgels.gabriel.blackbox.BlackBoxGradingEngine;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public final class FunctionalGradingEngine extends BlackBoxGradingEngine {
    private final FunctionalCompiler compiler;
    private final FunctionalEvaluator evaluator;
    private final SumAggregator aggregator;

    private CppFamilyGradingLanguage gradingLanguage;
    private CppFamilyGradingLanguage scorerLanguage;

    private Sandbox compilerSandbox;
    private Sandbox evaluatorSandbox;
    private Sandbox scorerSandbox;

    public FunctionalGradingEngine() {
        this.compiler = new FunctionalCompiler();
        this.evaluator = new FunctionalEvaluator();
        this.aggregator = new SumAggregator();
        this.gradingLanguage = new Cpp11GradingLanguage();
        this.scorerLanguage = new Cpp11GradingLanguage();
    }

    @Override
    public String getName() {
        return "Functional";
    }

    @Override
    public GradingConfig createDefaultGradingConfig() {
        return new FunctionalGradingConfig.Builder()
                .timeLimit(getDefaultCompilationTimeLimitInMilliseconds())
                .memoryLimit(getDefaultMemoryLimitInKilobytes())
                .addTestData(TestGroup.of(0, ImmutableList.of()))
                .addTestData(TestGroup.of(-1, ImmutableList.of()))
                .build();
    }

    @Override
    public GradingConfig createGradingConfigFromJson(String json) throws IOException {
        return MAPPER.readValue(json, FunctionalGradingConfig.class);
    }

    @Override
    protected void prepareAlgorithms(GradingConfig config, GradingLanguage language, Map<String, File> sourceFiles, Map<String, File> helperFiles, SandboxFactory sandboxFactory) throws PreparationException {
        FunctionalGradingConfig castConfig = (FunctionalGradingConfig) config;

        compilerSandbox = sandboxFactory.newSandbox();
        compiler.prepare(compilerSandbox, getCompilationDir(), gradingLanguage, helperFiles);

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

        evaluatorSandbox = sandboxFactory.newSandbox();
        evaluator.prepare(evaluatorSandbox, scorer, getCompilationDir(), getEvaluationDir(), castConfig.getTimeLimit(), castConfig.getMemoryLimit());
    }

    void setGradingLanguage(CppFamilyGradingLanguage gradingLanguage) {
        this.gradingLanguage = gradingLanguage;
    }

    public void setScorerLanguage(CppFamilyGradingLanguage scorerLanguage) {
        this.scorerLanguage = scorerLanguage;
    }

    @Override
    protected TestCaseAggregator getAggregator() {
        return aggregator;
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
    protected void cleanUp() {
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
