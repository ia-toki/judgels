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
import org.iatoki.judgels.gabriel.blackbox.Scorer;
import org.iatoki.judgels.gabriel.blackbox.TestGroup;
import org.iatoki.judgels.gabriel.blackbox.algorithms.FunctionalCompiler;
import org.iatoki.judgels.gabriel.blackbox.algorithms.FunctionalEvaluator;
import org.iatoki.judgels.gabriel.blackbox.algorithms.IdentityScorer;
import org.iatoki.judgels.gabriel.blackbox.algorithms.SimpleReducer;
import org.iatoki.judgels.gabriel.blackbox.configs.FunctionalGradingConfig;
import org.iatoki.judgels.gabriel.blackbox.languages.Cpp11GradingLanguage;
import org.iatoki.judgels.gabriel.sandboxes.Sandbox;
import org.iatoki.judgels.gabriel.sandboxes.SandboxFactory;

import java.io.File;
import java.util.Map;

public final class FunctionalGradingEngine extends BlackBoxGradingEngine {

    private Compiler compiler;
    private Evaluator evaluator;
    private Scorer scorer;
    private Reducer reducer;

    private GradingLanguage gradingLanguage;

    private Sandbox compilerSandbox;
    private Sandbox evaluatorSandbox;

    public FunctionalGradingEngine() {
        this.gradingLanguage = new Cpp11GradingLanguage();
    }

    @Override
    public String getName() {
        return "Functional";
    }

    @Override
    public GradingConfig createDefaultGradingConfig() {
        return new FunctionalGradingConfig(getDefaultCompilationTimeLimitInMilliseconds(), getDefaultMemoryLimitInKilobytes(), ImmutableList.of(new TestGroup(0, ImmutableList.of())), ImmutableList.of());
    }

    @Override
    public GradingConfig createGradingConfigFromJson(String json) {
        return new Gson().fromJson(json, FunctionalGradingConfig.class);
    }

    @Override
    protected void prepareAlgorithms(BlackBoxGradingConfig config, GradingLanguage language, Map<String, File> sourceFiles, Map<String, File> helperFiles, SandboxFactory sandboxFactory) throws PreparationException {
        FunctionalGradingConfig castConfig = (FunctionalGradingConfig) config;

        compilerSandbox = sandboxFactory.newSandbox();
        compiler = new FunctionalCompiler(compilerSandbox, getCompilationDir(), gradingLanguage, sourceFiles, helperFiles, getCompilationTimeLimitInMilliseconds(), getCompilationMemoryLimitInKilobytes());
        evaluatorSandbox = sandboxFactory.newSandbox();
        evaluator = new FunctionalEvaluator(evaluatorSandbox, getCompilationDir(), getEvaluationDir(), castConfig.getTimeLimitInMilliseconds(), castConfig.getMemoryLimitInKilobytes());
        scorer = new IdentityScorer();
        reducer = new SimpleReducer();
    }

    void setGradingLanguage(GradingLanguage gradingLanguage) {
        this.gradingLanguage = gradingLanguage;
    }

    @Override
    protected Reducer getReducer() {
        return reducer;
    }

    @Override
    protected Scorer getScorer() {
        return scorer;
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
    protected void cleanUp() {
        if (compilerSandbox != null) {
            compilerSandbox.cleanUp();
        }
        if (evaluatorSandbox != null) {
            evaluatorSandbox.cleanUp();
        }
    }
}
