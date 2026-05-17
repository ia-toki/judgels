package judgels.grading.engines.interactive;

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
import judgels.grading.api.TestGroup;
import judgels.grading.compilers.SingleSourceFileCompiler;
import judgels.grading.engines.BlackboxGradingEngine;
import judgels.grading.engines.DefaultConfig;
import judgels.grading.helpers.communicator.Communicator;

public class InteractiveWithSubtasksGradingEngine extends BlackboxGradingEngine {
    private final SingleSourceFileCompiler compiler;
    private final InteractiveEvaluator evaluator;
    private final MinAggregator aggregator;

    private Sandbox compilerSandbox;
    private Sandbox solutionSandbox;
    private Sandbox communicatorSandbox;

    public InteractiveWithSubtasksGradingEngine() {
        this.compiler = new SingleSourceFileCompiler();
        this.evaluator = new InteractiveEvaluator();
        this.aggregator = new MinAggregator();
    }

    @Override
    public String getName() {
        return "Interactive with Subtasks";
    }

    @Override
    public GradingConfig createDefaultConfig() {
        return new InteractiveWithSubtasksGradingConfig.Builder()
                .timeLimit(DefaultConfig.TIME_LIMIT)
                .memoryLimit(DefaultConfig.MEMORY_LIMIT)
                .addTestData(TestGroup.of(0, ImmutableList.of()))
                .build();
    }

    @Override
    public GradingConfig parseConfig(ObjectMapper mapper, String json) throws IOException {
        return mapper.readValue(json, InteractiveWithSubtasksGradingConfig.class);
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

        InteractiveWithSubtasksGradingConfig cfg = (InteractiveWithSubtasksGradingConfig) config;
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
