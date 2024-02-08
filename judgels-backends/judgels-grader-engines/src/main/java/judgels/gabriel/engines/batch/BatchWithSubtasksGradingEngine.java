package judgels.gabriel.engines.batch;

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
import judgels.gabriel.compilers.SingleSourceFileCompiler;
import judgels.gabriel.engines.BlackboxGradingEngine;
import judgels.gabriel.engines.DefaultConfig;
import judgels.gabriel.helpers.scorer.ScorerRegistry;

public class BatchWithSubtasksGradingEngine extends BlackboxGradingEngine {
    private final SingleSourceFileCompiler compiler;
    private final BatchEvaluator evaluator;
    private final MinAggregator aggregator;

    private Sandbox compilerSandbox;
    private Sandbox evaluatorSandbox;
    private Sandbox scorerSandbox;

    public BatchWithSubtasksGradingEngine() {
        this.compiler = new SingleSourceFileCompiler();
        this.evaluator = new BatchEvaluator();
        this.aggregator = new MinAggregator();
    }

    @Override
    public String getName() {
        return "Batch with Subtasks";
    }

    @Override
    public GradingConfig createDefaultConfig() {
        return new BatchWithSubtasksGradingConfig.Builder()
                .timeLimit(DefaultConfig.TIME_LIMIT)
                .memoryLimit(DefaultConfig.MEMORY_LIMIT)
                .addTestData(TestGroup.of(0, ImmutableList.of()))
                .build();
    }

    @Override
    public GradingConfig parseConfig(ObjectMapper mapper, String json) throws IOException {
        return mapper.readValue(json, BatchWithSubtasksGradingConfig.class);
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

        BatchWithSubtasksGradingConfig cfg = (BatchWithSubtasksGradingConfig) config;

        String sourceKey = config.getSourceFileFields().keySet().iterator().next();
        File sourceFile = sourceFiles.get(sourceKey);

        compilerSandbox = sandboxFactory.newSandbox();
        compiler.prepare(compilerSandbox, compilationDir, language);

        if (cfg.getCustomScorer().isPresent()) {
            scorerSandbox = sandboxFactory.newSandbox();
        }
        Scorer scorer = ScorerRegistry.getAndPrepare(cfg.getCustomScorer(), helperFiles, scorerSandbox, evaluationDir);

        evaluatorSandbox = sandboxFactory.newSandbox();
        evaluator.prepare(
                evaluatorSandbox,
                scorer, compilationDir,
                evaluationDir,
                language,
                sourceFile,
                cfg.getTimeLimit(),
                cfg.getMemoryLimit(),
                false);
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
