package org.iatoki.judgels.gabriel.blackbox.engines;

import com.google.common.collect.ImmutableList;
import judgels.gabriel.aggregators.SumAggregator;
import judgels.gabriel.api.Compiler;
import judgels.gabriel.api.Evaluator;
import judgels.gabriel.api.GradingLanguage;
import judgels.gabriel.api.PreparationException;
import judgels.gabriel.api.Sandbox;
import judgels.gabriel.api.SandboxFactory;
import judgels.gabriel.api.TestCaseAggregator;
import judgels.gabriel.api.GradingConfig;
import judgels.gabriel.api.TestGroup;
import judgels.gabriel.compilers.SingleSourceFileCompiler;
import judgels.gabriel.engines.batch.BatchGradingConfig;
import judgels.gabriel.languages.cpp.Cpp11GradingLanguage;
import org.iatoki.judgels.gabriel.blackbox.BlackBoxGradingEngine;
import org.iatoki.judgels.gabriel.blackbox.Scorer;
import org.iatoki.judgels.gabriel.blackbox.algorithms.BatchEvaluator;
import org.iatoki.judgels.gabriel.blackbox.algorithms.CustomScorer;
import org.iatoki.judgels.gabriel.blackbox.algorithms.DiffScorer;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public final class BatchGradingEngine extends BlackBoxGradingEngine {

    private Compiler compiler;
    private Evaluator evaluator;
    private Scorer scorer;
    private TestCaseAggregator aggregator;

    private Sandbox compilerSandbox;
    private Sandbox evaluatorSandbox;
    private Sandbox scorerSandbox;

    private int scoringTimeLimit;
    private int scoringMemoryLimit;
    private GradingLanguage scorerLanguage;

    public BatchGradingEngine() {
        this.compiler = new SingleSourceFileCompiler();
        this.scoringTimeLimit = 10000;
        this.scoringMemoryLimit = 1024 * 1024;
        this.scorerLanguage = new Cpp11GradingLanguage();
    }

    @Override
    protected void prepareAlgorithms(GradingConfig config, GradingLanguage language, Map<String, File> sourceFiles, Map<String, File> helperFiles, SandboxFactory sandboxFactory) throws PreparationException {
        String sourceFieldKey = config.getSourceFileFields().keySet().iterator().next();
        File sourceFile = sourceFiles.get(sourceFieldKey);
        BatchGradingConfig castConfig = (BatchGradingConfig) config;

        compilerSandbox = sandboxFactory.newSandbox();
        compiler.prepare(compilerSandbox, getCompilationDir(), language, helperFiles, getCompilationTimeLimitInMilliseconds(), getCompilationMemoryLimitInKilobytes());

        evaluatorSandbox = sandboxFactory.newSandbox();
        evaluator = new BatchEvaluator(evaluatorSandbox, getCompilationDir(), getEvaluationDir(), language, sourceFile, castConfig.getTimeLimit(), castConfig.getMemoryLimit());

        if (castConfig.getCustomScorer().isPresent()) {
            scorerSandbox = sandboxFactory.newSandbox();
            File scorerFile = helperFiles.get(castConfig.getCustomScorer().get());
            scorer = new CustomScorer(scorerSandbox, getScoringDir(), scorerLanguage, scorerFile, getCompilationTimeLimitInMilliseconds(), getCompilationMemoryLimitInKilobytes(), scoringTimeLimit, scoringMemoryLimit);
        } else {
            scorer = new DiffScorer();
        }

        aggregator = new SumAggregator();
    }

    public void setScoringTimeLimit(int scoringTimeLimit) {
        this.scoringTimeLimit = scoringTimeLimit;
    }

    public void setScoringMemoryLimit(int scoringMemoryLimit) {
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
    protected TestCaseAggregator getAggregator() {
        return aggregator;
    }

    @Override
    public String getName() {
        return "Batch";
    }

    @Override
    public GradingConfig createDefaultGradingConfig() {
        return new BatchGradingConfig.Builder()
                .timeLimit(getDefaultCompilationTimeLimitInMilliseconds())
                .memoryLimit(getDefaultMemoryLimitInKilobytes())
                .addTestData(TestGroup.of(0, ImmutableList.of()))
                .addTestData(TestGroup.of(-1, ImmutableList.of()))
                .build();
    }

    @Override
    public GradingConfig createGradingConfigFromJson(String json) throws IOException {
        return MAPPER.readValue(json, BatchGradingConfig.class);
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
