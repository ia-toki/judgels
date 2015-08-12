package org.iatoki.judgels.gabriel.blackbox.engines;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import org.iatoki.judgels.gabriel.GradingConfig;
import org.iatoki.judgels.gabriel.GradingLanguage;
import org.iatoki.judgels.gabriel.blackbox.BlackBoxGradingConfig;
import org.iatoki.judgels.gabriel.blackbox.BlackBoxGradingEngine;
import org.iatoki.judgels.gabriel.blackbox.Compiler;
import org.iatoki.judgels.gabriel.blackbox.Evaluator;
import org.iatoki.judgels.gabriel.blackbox.PreparationException;
import org.iatoki.judgels.gabriel.blackbox.Reducer;
import org.iatoki.judgels.gabriel.blackbox.Sandbox;
import org.iatoki.judgels.gabriel.blackbox.SandboxFactory;
import org.iatoki.judgels.gabriel.blackbox.Scorer;
import org.iatoki.judgels.gabriel.blackbox.TestGroup;
import org.iatoki.judgels.gabriel.blackbox.algorithms.IdentityScorer;
import org.iatoki.judgels.gabriel.blackbox.algorithms.InteractiveEvaluator;
import org.iatoki.judgels.gabriel.blackbox.algorithms.SimpleReducer;
import org.iatoki.judgels.gabriel.blackbox.algorithms.SingleSourceFileCompiler;
import org.iatoki.judgels.gabriel.blackbox.configs.InteractiveGradingConfig;
import org.iatoki.judgels.gabriel.blackbox.languages.Cpp11GradingLanguage;

import java.io.File;
import java.util.Map;

public final class InteractiveGradingEngine extends BlackBoxGradingEngine {
    private org.iatoki.judgels.gabriel.blackbox.Compiler compiler;
    private Evaluator evaluator;
    private Scorer scorer;
    private Reducer reducer;

    private Sandbox compilerSandbox;
    private Sandbox evaluatorContestantSandbox;
    private Sandbox evaluatorCommunicatorSandbox;

    private GradingLanguage communicatorLanguage;

    public InteractiveGradingEngine() {
        this.communicatorLanguage = new Cpp11GradingLanguage();
    }

    @Override
    public String getName() {
        return "Interactive";
    }

    @Override
    protected void prepare(SandboxFactory sandboxFactory, File workingDir, BlackBoxGradingConfig config, GradingLanguage language, Map<String, File> sourceFiles, Map<String, File> helperFiles) throws PreparationException {
        InteractiveGradingConfig castConfig = (InteractiveGradingConfig) config;
        if (castConfig.getCommunicator() == null) {
            throw new PreparationException("Communicator not specified");
        }

        String sourceFieldKey = config.getSourceFileFields().keySet().iterator().next();

        File contestantSourceFile = sourceFiles.get(sourceFieldKey);
        File communicatorSourceFile = helperFiles.get(castConfig.getCommunicator());

        prepareWorkingDirs(workingDir);

        compilerSandbox = sandboxFactory.newSandbox();
        compiler = new SingleSourceFileCompiler(compilerSandbox, getCompilationDir(), language, sourceFieldKey, contestantSourceFile, getCompilationTimeLimitInMilliseconds(), getCompilationMemoryLimitInKilobytes());

        evaluatorContestantSandbox = sandboxFactory.newSandbox();
        evaluatorCommunicatorSandbox = sandboxFactory.newSandbox();

        evaluator = new InteractiveEvaluator(evaluatorContestantSandbox, evaluatorCommunicatorSandbox, sandboxFactory.newSandboxesInteractor(), getCompilationDir(), getEvaluationDir(), language, communicatorLanguage, contestantSourceFile, communicatorSourceFile,  getCompilationTimeLimitInMilliseconds(), getCompilationMemoryLimitInKilobytes(), castConfig.getTimeLimitInMilliseconds(), castConfig.getMemoryLimitInKilobytes());
        scorer = new IdentityScorer(getEvaluationDir());
        reducer = new SimpleReducer();
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
        return new InteractiveGradingConfig(getDefaultCompilationTimeLimitInMilliseconds(), getDefaultMemoryLimitInKilobytes(), ImmutableList.of(new TestGroup(0, ImmutableList.of())), null);
    }

    @Override
    public GradingConfig createGradingConfigFromJson(String json) {
        return new Gson().fromJson(json, InteractiveGradingConfig.class);
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
