package judgels.grading.engines.outputonly;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import judgels.grading.aggregators.SumAggregator;
import judgels.grading.api.Aggregator;
import judgels.grading.api.Compiler;
import judgels.grading.api.Evaluator;
import judgels.grading.api.GradingConfig;
import judgels.grading.api.GradingLanguage;
import judgels.grading.api.GradingOptions;
import judgels.grading.api.PreparationException;
import judgels.grading.api.Sandbox;
import judgels.grading.api.SandboxFactory;
import judgels.grading.api.Scorer;
import judgels.grading.api.TestGroup;
import judgels.grading.compilers.NoOpCompiler;
import judgels.grading.engines.BlackboxGradingEngine;
import judgels.grading.helpers.OutputOnlyEvaluator;
import judgels.grading.helpers.scorer.ScorerRegistry;

public class OutputOnlyGradingEngine extends BlackboxGradingEngine {
    private final NoOpCompiler compiler;
    private final OutputOnlyEvaluator evaluator;
    private final SumAggregator aggregator;

    private Sandbox scorerSandbox;

    public OutputOnlyGradingEngine() {
        this.compiler = new NoOpCompiler();
        this.evaluator = new OutputOnlyEvaluator();
        this.aggregator = new SumAggregator();
    }

    @Override
    public String getName() {
        return "Output Only";
    }

    @Override
    public GradingConfig createDefaultConfig() {
        return new OutputOnlyGradingConfig.Builder()
                .addTestData(TestGroup.of(0, ImmutableList.of()))
                .addTestData(TestGroup.of(-1, ImmutableList.of()))
                .build();
    }

    @Override
    public GradingConfig parseConfig(ObjectMapper mapper, String json) throws IOException {
        return mapper.readValue(json, OutputOnlyGradingConfig.class);
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
            GradingOptions options,
            GradingLanguage language,
            Map<String, File> sourceFiles,
            Map<String, File> helperFiles,
            SandboxFactory sandboxFactory,
            File compilationDir,
            File evaluationDir) throws PreparationException {

        OutputOnlyGradingConfig cfg = (OutputOnlyGradingConfig) config;

        String sourceKey = config.getSourceFileFields().keySet().iterator().next();
        File sourceFile = sourceFiles.get(sourceKey);

        if (cfg.getCustomScorer().isPresent()) {
            scorerSandbox = sandboxFactory.newSandbox();
        }
        Scorer scorer = ScorerRegistry.getAndPrepare(cfg.getCustomScorer(), helperFiles, scorerSandbox, evaluationDir);

        evaluator.prepare(scorer, evaluationDir, sourceFile);
    }

    @Override
    public void cleanUp() {
        if (scorerSandbox != null) {
            scorerSandbox.cleanUp();
        }
    }
}
