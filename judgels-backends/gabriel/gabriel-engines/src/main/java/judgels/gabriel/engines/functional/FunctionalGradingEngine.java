package judgels.gabriel.engines.functional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import judgels.gabriel.aggregators.SumAggregator;
import judgels.gabriel.api.Aggregator;
import judgels.gabriel.api.Compiler;
import judgels.gabriel.api.Evaluator;
import judgels.gabriel.api.GradingConfig;
import judgels.gabriel.api.GradingLanguage;
import judgels.gabriel.api.PreparationException;
import judgels.gabriel.api.Sandbox;
import judgels.gabriel.api.SandboxFactory;
import judgels.gabriel.api.Scorer;
import judgels.gabriel.api.TestGroup;
import judgels.gabriel.compilers.FunctionalCompiler;
import judgels.gabriel.engines.BlackboxGradingEngine;
import judgels.gabriel.engines.DefaultConfig;
import judgels.gabriel.helpers.FunctionalEvaluator;
import judgels.gabriel.helpers.scorer.ScorerRegistry;

public class FunctionalGradingEngine extends BlackboxGradingEngine {
    private final FunctionalCompiler compiler;
    private final FunctionalEvaluator evaluator;
    private final SumAggregator aggregator;

    private Sandbox compilerSandbox;
    private Sandbox evaluatorSandbox;
    private Sandbox scorerSandbox;

    public FunctionalGradingEngine() {
        this.compiler = new FunctionalCompiler();
        this.evaluator = new FunctionalEvaluator();
        this.aggregator = new SumAggregator();
    }

    @Override
    public String getName() {
        return "Functional";
    }

    @Override
    public GradingConfig createDefaultConfig() {
        return new FunctionalGradingConfig.Builder()
                .timeLimit(DefaultConfig.TIME_LIMIT)
                .memoryLimit(DefaultConfig.MEMORY_LIMIT)
                .addTestData(TestGroup.of(0, ImmutableList.of()))
                .addTestData(TestGroup.of(-1, ImmutableList.of()))
                .build();
    }

    @Override
    public GradingConfig parseConfig(ObjectMapper mapper, String json) throws IOException {
        return mapper.readValue(json, FunctionalGradingConfig.class);
    }

    @Override
    public Compiler getCompiler() {
        return compiler;
    }

    @Override
    public Evaluator getEvaluator() {
        return evaluator;
    }

    @Override
    public Aggregator getAggregator() {
        return aggregator;
    }

    @Override
    public void prepare(
            GradingConfig config,
            GradingLanguage language,
            Map<String, File> sourceFiles,
            Map<String, File> helperFiles,
            SandboxFactory sandboxFactory,
            File compilationDir,
            File evaluationDir) throws PreparationException {

        FunctionalGradingConfig cfg = (FunctionalGradingConfig) config;

        compilerSandbox = sandboxFactory.newSandbox();
        compiler.prepare(compilerSandbox, compilationDir, language, helperFiles);

        if (cfg.getCustomScorer().isPresent()) {
            scorerSandbox = sandboxFactory.newSandbox();
        }
        Scorer scorer = ScorerRegistry.getAndPrepare(cfg.getCustomScorer(), helperFiles, scorerSandbox, evaluationDir);

        evaluatorSandbox = sandboxFactory.newSandbox();
        evaluator.prepare(
                evaluatorSandbox,
                scorer,
                compilationDir,
                evaluationDir,
                cfg.getTimeLimit(),
                cfg.getMemoryLimit());
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
