package org.iatoki.judgels.gabriel.blackbox.engines;

import com.google.common.collect.ImmutableList;
import judgels.gabriel.aggregators.MinAggregator;
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
import judgels.gabriel.compilers.SingleSourceFileCompiler;
import judgels.gabriel.engines.batch.BatchWithSubtasksGradingConfig;
import judgels.gabriel.evaluators.BatchEvaluator;
import judgels.gabriel.evaluators.helpers.CustomScorer;
import judgels.gabriel.evaluators.helpers.DiffScorer;
import judgels.gabriel.languages.cpp.Cpp11GradingLanguage;
import judgels.gabriel.languages.cpp.CppFamilyGradingLanguage;
import org.iatoki.judgels.gabriel.blackbox.BlackBoxGradingEngine;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public final class BatchWithSubtasksGradingEngine extends BlackBoxGradingEngine {
    private final SingleSourceFileCompiler compiler;
    private final BatchEvaluator evaluator;
    private final MinAggregator aggregator;

    private Sandbox compilerSandbox;
    private Sandbox evaluatorSandbox;
    private Sandbox scorerSandbox;

    private CppFamilyGradingLanguage scorerLanguage;

    public BatchWithSubtasksGradingEngine() {
        this.compiler = new SingleSourceFileCompiler();
        this.evaluator = new BatchEvaluator();
        this.aggregator = new MinAggregator();
        this.scorerLanguage = new Cpp11GradingLanguage();
    }

    @Override
    public String getName() {
        return "Batch with Subtasks";
    }

    @Override
    public GradingConfig createDefaultGradingConfig() {
        return new BatchWithSubtasksGradingConfig.Builder()
                .timeLimit(getDefaultCompilationTimeLimitInMilliseconds())
                .memoryLimit(getDefaultMemoryLimitInKilobytes())
                .addTestData(TestGroup.of(0, ImmutableList.of()))
                .build();
    }

    @Override
    public GradingConfig createGradingConfigFromJson(String json) throws IOException {
        return MAPPER.readValue(json, BatchWithSubtasksGradingConfig.class);
    }

    @Override
    protected void prepareAlgorithms(GradingConfig config, GradingLanguage language, Map<String, File> sourceFiles, Map<String, File> helperFiles, SandboxFactory sandboxFactory) throws PreparationException {
        String sourceFieldKey = config.getSourceFileFields().keySet().iterator().next();
        File sourceFile = sourceFiles.get(sourceFieldKey);
        BatchWithSubtasksGradingConfig castConfig = (BatchWithSubtasksGradingConfig) config;

        compilerSandbox = sandboxFactory.newSandbox();
        compiler.prepare(compilerSandbox, getCompilationDir(), language);

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
        evaluator.prepare(evaluatorSandbox, scorer, getCompilationDir(), getEvaluationDir(), language, sourceFile, castConfig.getTimeLimit(), castConfig.getMemoryLimit());
    }

    public void setScorerLanguage(CppFamilyGradingLanguage scorerLanguage) {
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
    protected TestCaseAggregator getAggregator() {
        return aggregator;
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
