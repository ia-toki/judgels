package judgels.grading.engines.functional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import judgels.grading.aggregators.MinAggregator;
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
import judgels.grading.compilers.FunctionalCompiler;
import judgels.grading.engines.BlackboxGradingEngine;
import judgels.grading.engines.DefaultConfig;
import judgels.grading.helpers.FunctionalEvaluator;
import judgels.grading.helpers.scorer.ScorerRegistry;

public class FunctionalWithSubtasksGradingEngine extends BlackboxGradingEngine {
    private final FunctionalCompiler compiler;
    private final FunctionalEvaluator evaluator;
    private final MinAggregator aggregator;

    private Sandbox compilerSandbox;
    private Sandbox evaluatorSandbox;
    private Sandbox scorerSandbox;

    public FunctionalWithSubtasksGradingEngine() {
        this.compiler = new FunctionalCompiler();
        this.evaluator = new FunctionalEvaluator();
        this.aggregator = new MinAggregator();
    }

    @Override
    public String getName() {
        return "Functional with Subtasks";
    }

    @Override
    public GradingConfig createDefaultConfig() {
        return new FunctionalWithSubtasksGradingConfig.Builder()
                .timeLimit(DefaultConfig.TIME_LIMIT)
                .memoryLimit(DefaultConfig.MEMORY_LIMIT)
                .addTestData(TestGroup.of(0, ImmutableList.of()))
                .build();
    }

    @Override
    public GradingConfig parseConfig(ObjectMapper mapper, String json) throws IOException {
        return mapper.readValue(json, FunctionalWithSubtasksGradingConfig.class);
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

        FunctionalWithSubtasksGradingConfig cfg = (FunctionalWithSubtasksGradingConfig) config;

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
                language,
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
