package org.iatoki.judgels.gabriel.blackbox;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.apache.commons.io.FileUtils;
import org.iatoki.judgels.gabriel.FakeSealtiel;
import org.iatoki.judgels.gabriel.GraderProperties;
import org.iatoki.judgels.gabriel.GradingExecutor;
import org.iatoki.judgels.gabriel.GradingRegistry;
import org.iatoki.judgels.gabriel.GradingRunner;
import org.iatoki.judgels.gabriel.Language;
import org.iatoki.judgels.gabriel.LanguageRegistry;
import org.iatoki.judgels.gabriel.SandboxProvider;
import org.iatoki.judgels.gabriel.grading.batch.BatchCompilationExecutor;
import org.iatoki.judgels.gabriel.grading.batch.BatchGradingConfig;
import org.iatoki.judgels.gabriel.sandboxes.FakeSandboxProvider;
import org.iatoki.judgels.sealtiel.client.ClientMessage;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public final class BlackBoxGradingRunner implements GradingRunner {

    private final long id;
    private final FakeSealtiel sealtiel;
    private final BlackBoxGradingRequest request;

    public BlackBoxGradingRunner(long id, FakeSealtiel sealtiel, BlackBoxGradingRequest request) {
        this.id = id;
        this.sealtiel = sealtiel;
        this.request = request;
    }

    @Override
    public void run() {
        GradingVerdict verdict = grade();
        BlackBoxGradingResult result = new BlackBoxGradingResult(request.getSubmissionJid(), verdict);
        ClientMessage message = new ClientMessage(request.getSenderChannel(), "BlackBoxGradingResult", new Gson().toJson(result));
        sealtiel.sendMessage(message);
    }

    private GradingVerdict grade() {
        Language language;
        BlackBoxGradingExecutor executor;

        try {
            language = loadLanguage(request.getLanguage());
            executor = (BlackBoxGradingExecutor) loadGradingExecutor(request.getGradingType());
        } catch (IllegalArgumentException | ClassCastException e) {
            return createInternalErrorVerdict(e.getMessage());
        }

        GraderProperties prop = GraderProperties.getInstance();

        SandboxProvider sandboxProvider = new FakeSandboxProvider(prop.getSandboxDir());

        File gradingDir = new File(prop.getGradingCacheDir(), request.getProblemJid());

        gradingDir = new File(gradingDir, "grading");

        BatchGradingConfig config;

        try {
            String configAsJson = FileUtils.readFileToString(new File(gradingDir, "config.json"));
            config = new Gson().fromJson(configAsJson, BatchGradingConfig.class);
        } catch (IOException | JsonSyntaxException e) {
            return createInternalErrorVerdict("Cannot read config.json");
        }

        File sourceDir = new File(new File(prop.getTempDir(), "" + id), "source");

        try {

            FileUtils.forceMkdir(sourceDir);
            for (Map.Entry<String, byte[]> entry : request.getSourceFiles().entrySet()) {
                FileUtils.writeByteArrayToFile(new File(sourceDir, entry.getKey()), entry.getValue());
            }
        } catch (IOException e) {
            return createInternalErrorVerdict("Cannot save submission file");
        }


        File testDataDir = new File(gradingDir, "testData");
        File helperDir = new File(gradingDir, "helper");

        return executor.grade(sandboxProvider, language, sourceDir, helperDir, testDataDir, config);
    }

    @Override
    public long getId() {
        return id;
    }

    private Language loadLanguage(String languageName) throws IllegalArgumentException {
        return LanguageRegistry.getInstance().getLanguage(languageName);
    }

    private GradingExecutor loadGradingExecutor(String gradingType) throws IllegalArgumentException {
        return GradingRegistry.getInstance().getGradingExecutor(gradingType);
    }

    private GradingVerdict createInternalErrorVerdict(String message) {
        return new GradingVerdict(OverallVerdict.INTERNAL_ERROR, 0, new GradingVerdictDetails(message, null, null, null));
    }
}
