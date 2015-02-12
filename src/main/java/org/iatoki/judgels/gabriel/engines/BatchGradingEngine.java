package org.iatoki.judgels.gabriel.engines;

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

import java.io.File;
import java.util.Map;

public final class BatchGradingEngine extends BlackBoxGradingEngine {

    private Compiler compiler;
    private Evaluator evaluator;
    private Scorer scorer;
    private Reducer reducer;

    private Sandbox compilerSandbox;
    private Sandbox evaluatorSandbox;
    private Sandbox scorerSandbox;

    @Override
    protected void prepare(SandboxFactory sandboxFactory, File workingDir, BlackBoxGradingConfig config, GradingLanguage language, Map<String, File> sourceFiles, Map<String, File> helperFiles) throws PreparationException {

    }

    @Override
    protected org.iatoki.judgels.gabriel.blackbox.Compiler getCompiler() {
        return null;
    }

    @Override
    protected Evaluator getEvaluator() {
        return null;
    }

    @Override
    protected Scorer getScorer() {
        return null;
    }

    @Override
    protected Reducer getReducer() {
        return null;
    }

    @Override
    public String getName() {
        return "Batch";
    }

    @Override
    public GradingConfig createDefaultGradingConfig() {
        return null;
    }

    @Override
    public GradingConfig createGradingConfigFromJson(String json) {
        return null;
    }

    @Override
    public void cleanUp() {

    }
}
