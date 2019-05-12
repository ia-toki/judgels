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
import judgels.gabriel.engines.interactive.InteractiveGradingConfig;
import judgels.gabriel.evaluators.InteractiveEvaluator;
import judgels.gabriel.evaluators.helpers.Communicator;
import judgels.gabriel.languages.cpp.Cpp11GradingLanguage;
import judgels.gabriel.languages.cpp.CppFamilyGradingLanguage;
import org.iatoki.judgels.gabriel.blackbox.BlackBoxGradingEngine;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public final class InteractiveGradingEngine extends BlackBoxGradingEngine {
    private final SingleSourceFileCompiler compiler;
    private final InteractiveEvaluator evaluator;
    private final SumAggregator aggregator;

    private Sandbox compilerSandbox;
    private Sandbox solutionSandbox;
    private Sandbox communicatorSandbox;

    private CppFamilyGradingLanguage communicatorLanguage;

    public InteractiveGradingEngine() {
        this.compiler = new SingleSourceFileCompiler();
        this.evaluator = new InteractiveEvaluator();
        this.aggregator = new SumAggregator();
        this.communicatorLanguage = new Cpp11GradingLanguage();
    }

    @Override
    public String getName() {
        return "Interactive";
    }

    @Override
    protected void prepareAlgorithms(GradingConfig config, GradingLanguage language, Map<String, File> sourceFiles, Map<String, File> helperFiles, SandboxFactory sandboxFactory) throws PreparationException {
        InteractiveGradingConfig castConfig = (InteractiveGradingConfig) config;
        if (!castConfig.getCommunicator().isPresent()) {
            throw new PreparationException("Communicator not specified");
        }

        String sourceFieldKey = config.getSourceFileFields().keySet().iterator().next();

        File contestantSourceFile = sourceFiles.get(sourceFieldKey);
        File communicatorSourceFile = helperFiles.get(castConfig.getCommunicator().get());

        compilerSandbox = sandboxFactory.newSandbox();
        compiler.prepare(compilerSandbox, getCompilationDir(), language);

        solutionSandbox = sandboxFactory.newSandbox();
        communicatorSandbox = sandboxFactory.newSandbox();

        Communicator communicator = new Communicator();
        communicator.prepare(solutionSandbox, communicatorSandbox, sandboxFactory.newSandboxInteractor(), getCompilationDir(), getEvaluationDir(), language, communicatorLanguage, contestantSourceFile, communicatorSourceFile, castConfig.getTimeLimit(), castConfig.getMemoryLimit());

        evaluator.prepare(communicator);
    }

    public void setCommunicatorLanguage(CppFamilyGradingLanguage communicatorLanguage) {
        this.communicatorLanguage = communicatorLanguage;
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
    public GradingConfig createDefaultGradingConfig() {
        return new InteractiveGradingConfig.Builder()
                .timeLimit(getDefaultCompilationTimeLimitInMilliseconds())
                .memoryLimit(getDefaultMemoryLimitInKilobytes())
                .addTestData(TestGroup.of(0, ImmutableList.of()))
                .addTestData(TestGroup.of(-1, ImmutableList.of()))
                .build();
    }

    @Override
    public GradingConfig createGradingConfigFromJson(String json) throws IOException {
        return MAPPER.readValue(json, InteractiveGradingConfig.class);
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
