package judgels.gabriel.engines.interactive;

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
import judgels.gabriel.api.GradingOptions;
import judgels.gabriel.api.PreparationException;
import judgels.gabriel.api.Sandbox;
import judgels.gabriel.api.SandboxFactory;
import judgels.gabriel.api.TestGroup;
import judgels.gabriel.compilers.SingleSourceFileCompiler;
import judgels.gabriel.engines.BlackboxGradingEngine;
import judgels.gabriel.engines.DefaultConfig;
import judgels.gabriel.helpers.communicator.Communicator;

public class InteractiveGradingEngine extends BlackboxGradingEngine {
    private final SingleSourceFileCompiler compiler;
    private final InteractiveEvaluator evaluator;
    private final SumAggregator aggregator;

    private Sandbox compilerSandbox;
    private Sandbox solutionSandbox;
    private Sandbox communicatorSandbox;

    public InteractiveGradingEngine() {
        this.compiler = new SingleSourceFileCompiler();
        this.evaluator = new InteractiveEvaluator();
        this.aggregator = new SumAggregator();
    }

    @Override
    public String getName() {
        return "Interactive";
    }

    @Override
    public GradingConfig createDefaultConfig() {
        return new InteractiveGradingConfig.Builder()
                .timeLimit(DefaultConfig.TIME_LIMIT)
                .memoryLimit(DefaultConfig.MEMORY_LIMIT)
                .addTestData(TestGroup.of(0, ImmutableList.of()))
                .addTestData(TestGroup.of(-1, ImmutableList.of()))
                .build();
    }

    @Override
    public GradingConfig parseConfig(ObjectMapper mapper, String json) throws IOException {
        return mapper.readValue(json, InteractiveGradingConfig.class);
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

        InteractiveGradingConfig cfg = (InteractiveGradingConfig) config;
        if (!cfg.getCommunicator().isPresent()) {
            throw new PreparationException("Communicator not specified");
        }

        String sourceKey = config.getSourceFileFields().keySet().iterator().next();
        File solutionSourceFile = sourceFiles.get(sourceKey);
        File communicatorSourceFile = helperFiles.get(cfg.getCommunicator().get());

        compilerSandbox = sandboxFactory.newSandbox();
        compiler.prepare(compilerSandbox, compilationDir, language);

        solutionSandbox = sandboxFactory.newSandbox();
        communicatorSandbox = sandboxFactory.newSandbox();
        Communicator communicator = new Communicator();
        communicator.prepare(
                solutionSandbox,
                communicatorSandbox,
                sandboxFactory.newSandboxInteractor(),
                compilationDir,
                evaluationDir,
                language,
                solutionSourceFile,
                communicatorSourceFile,
                cfg.getTimeLimit(),
                cfg.getMemoryLimit());

        evaluator.prepare(communicator);
    }

    @Override
    public void cleanUp() {
        if (compilerSandbox != null) {
            compilerSandbox.cleanUp();
        }
        if (solutionSandbox != null) {
            solutionSandbox.cleanUp();
        }
        if (communicatorSandbox != null) {
            communicatorSandbox.cleanUp();
        }
    }
}
