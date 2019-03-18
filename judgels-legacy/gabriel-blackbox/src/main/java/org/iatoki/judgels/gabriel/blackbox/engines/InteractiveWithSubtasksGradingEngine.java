package org.iatoki.judgels.gabriel.blackbox.engines;

import com.google.common.collect.ImmutableList;
import judgels.gabriel.api.GradingConfig;
import judgels.gabriel.api.TestGroup;
import judgels.gabriel.engines.interactive.InteractiveWithSubtasksGradingConfig;
import org.iatoki.judgels.gabriel.GradingLanguage;
import org.iatoki.judgels.gabriel.blackbox.BlackBoxGradingEngine;
import org.iatoki.judgels.gabriel.blackbox.Compiler;
import org.iatoki.judgels.gabriel.blackbox.Evaluator;
import org.iatoki.judgels.gabriel.blackbox.PreparationException;
import org.iatoki.judgels.gabriel.blackbox.Reducer;
import org.iatoki.judgels.gabriel.blackbox.Scorer;
import org.iatoki.judgels.gabriel.blackbox.algorithms.IdentityScorer;
import org.iatoki.judgels.gabriel.blackbox.algorithms.InteractiveEvaluator;
import org.iatoki.judgels.gabriel.blackbox.algorithms.SingleSourceFileCompiler;
import org.iatoki.judgels.gabriel.blackbox.algorithms.SubtaskReducer;
import org.iatoki.judgels.gabriel.blackbox.languages.Cpp11GradingLanguage;
import org.iatoki.judgels.gabriel.sandboxes.Sandbox;
import org.iatoki.judgels.gabriel.sandboxes.SandboxFactory;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public final class InteractiveWithSubtasksGradingEngine extends BlackBoxGradingEngine {

    private Compiler compiler;
    private Evaluator evaluator;
    private Scorer scorer;
    private Reducer reducer;

    private Sandbox compilerSandbox;
    private Sandbox evaluatorContestantSandbox;
    private Sandbox evaluatorCommunicatorSandbox;

    private GradingLanguage communicatorLanguage;

    public InteractiveWithSubtasksGradingEngine() {
        this.communicatorLanguage = new Cpp11GradingLanguage();
    }

    @Override
    public String getName() {
        return "Interactive with Subtasks";
    }

    @Override
    protected void prepareAlgorithms(GradingConfig config, GradingLanguage language, Map<String, File> sourceFiles, Map<String, File> helperFiles, SandboxFactory sandboxFactory) throws PreparationException {
        InteractiveWithSubtasksGradingConfig castConfig = (InteractiveWithSubtasksGradingConfig) config;
        if (!castConfig.getCommunicator().isPresent()) {
            throw new PreparationException("Communicator not specified");
        }

        String sourceFieldKey = config.getSourceFileFields().keySet().iterator().next();

        File contestantSourceFile = sourceFiles.get(sourceFieldKey);
        File communicatorSourceFile = helperFiles.get(castConfig.getCommunicator().get());

        compilerSandbox = sandboxFactory.newSandbox();
        compiler = new SingleSourceFileCompiler(compilerSandbox, getCompilationDir(), language, sourceFieldKey, contestantSourceFile, getCompilationTimeLimitInMilliseconds(), getCompilationMemoryLimitInKilobytes());

        evaluatorContestantSandbox = sandboxFactory.newSandbox();
        evaluatorCommunicatorSandbox = sandboxFactory.newSandbox();

        evaluator = new InteractiveEvaluator(evaluatorContestantSandbox, evaluatorCommunicatorSandbox, sandboxFactory.newSandboxesInteractor(), getCompilationDir(), getEvaluationDir(), language, communicatorLanguage, contestantSourceFile, communicatorSourceFile,  getCompilationTimeLimitInMilliseconds(), getCompilationMemoryLimitInKilobytes(), castConfig.getTimeLimit(), castConfig.getMemoryLimit());
        scorer = new IdentityScorer();
        reducer = new SubtaskReducer();
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
    protected Reducer getReducer() {
        return reducer;
    }

    @Override
    public GradingConfig createDefaultGradingConfig() {
        return new InteractiveWithSubtasksGradingConfig.Builder()
                .timeLimit(getDefaultCompilationTimeLimitInMilliseconds())
                .memoryLimit(getDefaultMemoryLimitInKilobytes())
                .addTestData(TestGroup.of(0, ImmutableList.of()))
                .build();
    }

    @Override
    public GradingConfig createGradingConfigFromJson(String json) throws IOException {
        return MAPPER.readValue(json, InteractiveWithSubtasksGradingConfig.class);
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
