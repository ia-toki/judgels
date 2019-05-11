package org.iatoki.judgels.gabriel.blackbox.engines;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
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
import judgels.gabriel.languages.cpp.Cpp11GradingLanguage;
import org.iatoki.judgels.gabriel.blackbox.BlackBoxGradingEngine;
import org.iatoki.judgels.gabriel.blackbox.Scorer;
import org.iatoki.judgels.gabriel.blackbox.algorithms.IdentityScorer;
import org.iatoki.judgels.gabriel.blackbox.algorithms.InteractiveEvaluator;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public final class InteractiveGradingEngine extends BlackBoxGradingEngine {
    private Compiler compiler;
    private Evaluator evaluator;
    private Scorer scorer;
    private TestCaseAggregator aggregator;

    private Sandbox compilerSandbox;
    private Sandbox evaluatorContestantSandbox;
    private Sandbox evaluatorCommunicatorSandbox;

    private GradingLanguage communicatorLanguage;

    public InteractiveGradingEngine() {
        this.compiler = new SingleSourceFileCompiler();
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
        compiler.prepare(compilerSandbox, getCompilationDir(), language, helperFiles, getCompilationTimeLimitInMilliseconds(), getCompilationMemoryLimitInKilobytes());

        evaluatorContestantSandbox = sandboxFactory.newSandbox();
        evaluatorCommunicatorSandbox = sandboxFactory.newSandbox();

        evaluator = new InteractiveEvaluator(evaluatorContestantSandbox, evaluatorCommunicatorSandbox, sandboxFactory.newSandboxInteractor(), getCompilationDir(), getEvaluationDir(), language, communicatorLanguage, contestantSourceFile, communicatorSourceFile,  getCompilationTimeLimitInMilliseconds(), getCompilationMemoryLimitInKilobytes(), castConfig.getTimeLimit(), castConfig.getMemoryLimit());
        scorer = new IdentityScorer();
        aggregator = new SumAggregator();
    }

    public void setCommunicatorLanguage(GradingLanguage communicatorLanguage) {
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
    protected Scorer getScorer() {
        return scorer;
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
        if (evaluatorContestantSandbox != null) {
            evaluatorContestantSandbox.cleanUp();
        }
        if (evaluatorCommunicatorSandbox != null) {
            evaluatorCommunicatorSandbox.cleanUp();
        }
    }
}
