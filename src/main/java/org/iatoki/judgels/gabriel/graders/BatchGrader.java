package org.iatoki.judgels.gabriel.graders;

import com.google.gson.Gson;
import org.iatoki.judgels.gabriel.Language;
import org.iatoki.judgels.gabriel.SandboxProvider;
import org.iatoki.judgels.gabriel.blackbox.*;
import org.iatoki.judgels.gabriel.blackbox.Compiler;
import org.iatoki.judgels.gabriel.steps.SimpleCompiler;
import org.iatoki.judgels.gabriel.steps.SimpleEvaluator;
import org.iatoki.judgels.gabriel.steps.SimpleReducer;
import org.iatoki.judgels.gabriel.steps.SubtaskScorer;

import java.io.File;
import java.util.Map;

public final class BatchGrader extends BlackBoxGrader {

    private Compiler compiler;
    private Evaluator evaluator;
    private Scorer scorer;
    private Reducer reducer;

    @Override
    public BlackBoxGradingConfig parseGradingConfigFromJson(String json) {
        return new Gson().fromJson(json, BlackBoxGradingConfig.class);
    }

    @Override
    protected void prepare(SandboxProvider sandboxProvider, File tempDir, BlackBoxGradingConfig config, Language language, Map<String, File> sourceFiles, Map<String, File> helperFiles) throws PreparationException {
        File sourceFile = sourceFiles.values().iterator().next();
        BatchGradingConfig batchConfig = (BatchGradingConfig) config;

        compiler = new SimpleCompiler(sandboxProvider.newSandbox(), tempDir, language, sourceFile);
        evaluator = new SimpleEvaluator(sandboxProvider.newSandbox(), tempDir, language, sourceFile, batchConfig.getTimeLimitInMilliseconds(), batchConfig.getMemoryLimitInKilobytes());
        scorer = new SubtaskScorer(tempDir);
        reducer = new SimpleReducer();
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
}
