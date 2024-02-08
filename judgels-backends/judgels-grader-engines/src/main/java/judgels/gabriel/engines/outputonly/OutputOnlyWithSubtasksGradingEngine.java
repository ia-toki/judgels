package judgels.gabriel.engines.outputonly;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import judgels.gabriel.aggregators.MinAggregator;
import judgels.gabriel.api.Aggregator;
import judgels.gabriel.api.Compiler;
import judgels.gabriel.api.Evaluator;
import judgels.gabriel.api.GradingConfig;
import judgels.gabriel.api.GradingLanguage;
import judgels.gabriel.api.GradingOptions;
import judgels.gabriel.api.PreparationException;
import judgels.gabriel.api.Sandbox;
import judgels.gabriel.api.SandboxFactory;
import judgels.gabriel.api.Scorer;
import judgels.gabriel.api.TestGroup;
import judgels.gabriel.compilers.NoOpCompiler;
import judgels.gabriel.engines.BlackboxGradingEngine;
import judgels.gabriel.helpers.OutputOnlyEvaluator;
import judgels.gabriel.helpers.scorer.ScorerRegistry;

public class OutputOnlyWithSubtasksGradingEngine extends BlackboxGradingEngine {
    private final NoOpCompiler compiler;
    private final OutputOnlyEvaluator evaluator;
    private final MinAggregator aggregator;

    private Sandbox scorerSandbox;

    public OutputOnlyWithSubtasksGradingEngine() {
        this.compiler = new NoOpCompiler();
        this.evaluator = new OutputOnlyEvaluator();
        this.aggregator = new MinAggregator();
    }

    @Override
    public String getName() {
        return "Output Only with Subtasks";
    }

    @Override
    public GradingConfig createDefaultConfig() {
        return new OutputOnlyWithSubtasksGradingConfig.Builder()
                .addTestData(TestGroup.of(0, ImmutableList.of()))
                .build();
    }

    @Override
    public GradingConfig parseConfig(ObjectMapper mapper, String json) throws IOException {
        return mapper.readValue(json, OutputOnlyWithSubtasksGradingConfig.class);
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

        OutputOnlyWithSubtasksGradingConfig cfg = (OutputOnlyWithSubtasksGradingConfig) config;

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
