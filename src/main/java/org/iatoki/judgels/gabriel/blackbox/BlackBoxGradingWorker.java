package org.iatoki.judgels.gabriel.blackbox;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.iatoki.judgels.gabriel.FakeClientMessage;
import org.iatoki.judgels.gabriel.FakeSealtiel;
import org.iatoki.judgels.gabriel.GabrielProperties;
import org.iatoki.judgels.gabriel.GraderRegistry;
import org.iatoki.judgels.gabriel.GradingException;
import org.iatoki.judgels.gabriel.GradingWorker;
import org.iatoki.judgels.gabriel.Language;
import org.iatoki.judgels.gabriel.LanguageRegistry;
import org.iatoki.judgels.gabriel.SandboxFactory;
import org.iatoki.judgels.gabriel.sandboxes.FakeSandboxFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public final class BlackBoxGradingWorker implements GradingWorker {
    private final String senderChannel;
    private final BlackBoxGradingRequest request;
    private final FakeSealtiel sealtiel;

    private File graderDir;
    private BlackBoxGrader grader;
    private BlackBoxGradingConfig config;
    private Language language;
    private SandboxFactory sandboxFactory;
    private Map<String, File> sourceFiles;
    private Map<String, File> helperFiles;
    private Map<String, File> testDataFiles;

    public BlackBoxGradingWorker(String senderChannel, BlackBoxGradingRequest request, FakeSealtiel sealtiel) {
        this.senderChannel = senderChannel;
        this.request = request;
        this.sealtiel = sealtiel;
    }

    @Override
    public String getId() {
        return request.getSubmissionJid();
    }

    @Override
    public void run() {
        BlackBoxGradingResult result;

        try {
            initialize();
            result = grader.gradeAfterInitialization(sandboxFactory, graderDir, language, sourceFiles, helperFiles, testDataFiles, config);
        } catch (GradingException e) {
            System.out.println("Grading id " + getId() + " error : " + e.getMessage());
            result = BlackBoxGradingResult.internalErrorResult();
        }

        grader.cleanUp();

        BlackBoxGradingResponse response = new BlackBoxGradingResponse(request.getSubmissionJid(), result);
        FakeClientMessage message = new FakeClientMessage(senderChannel, "BlackBoxGradingResponse", new Gson().toJson(response));

        sealtiel.sendMessage(message);
    }

    private void initialize() throws InitializationException {
        try {
            grader = (BlackBoxGrader) GraderRegistry.getInstance().getGrader(request.getGradingType());
            language = LanguageRegistry.getInstance().getLanguage(request.getGradingLanguage());

            File workerDir = getWorkerDir();
            sourceFiles = generateSourceFiles(workerDir);
            sandboxFactory = getSandboxProvider(workerDir);
            graderDir = getGraderDir(workerDir);

            File problemGradingDir = getProblemGradingDir(request.getProblemJid(), request.getProblemLastUpdate());
            helperFiles = generateHelperFiles(problemGradingDir);
            testDataFiles = generateTestDataFiles(problemGradingDir);
            config = parseGradingConfig(problemGradingDir, grader);
        } catch (IOException | IllegalArgumentException e) {
            throw new InitializationException(e.getMessage());
        }
    }

    private File getWorkerDir() throws IOException {
        File runnerDir = new File(GabrielProperties.getInstance().getWorkerDir(), getId());
        FileUtils.forceMkdir(runnerDir);
        return runnerDir;
    }

    private SandboxFactory getSandboxProvider(File workerDir) throws IOException {
        File sandboxesDir = new File(workerDir, "sandbox");
        FileUtils.forceMkdir(sandboxesDir);

        return new FakeSandboxFactory(sandboxesDir, ImmutableList.of());
    }

    private File getGraderDir(File workerDir) throws IOException {
        File tempDir = new File(workerDir, "grader");
        FileUtils.forceMkdir(tempDir);

        return tempDir;
    }

    private File getProblemGradingDir(String problemJid, long problemLastUpdate) throws IOException {
        return new File(GabrielProperties.getInstance().getProblemDir(), problemJid);
    }

    private Map<String, File> generateHelperFiles(File problemGradingDir) throws FileNotFoundException {
        File helperDir = new File(problemGradingDir, "helper");
        return listFilesAsMap(helperDir);
    }

    private Map<String, File> generateTestDataFiles(File problemGradingDir) throws FileNotFoundException {
        File testDatDir = new File(problemGradingDir, "testdata");
        return listFilesAsMap(testDatDir);
    }

    private BlackBoxGradingConfig parseGradingConfig(File problemGradirDir, BlackBoxGrader grader) throws IOException {
        File config = new File(problemGradirDir, "config.json");
        String configAsJson = FileUtils.readFileToString(config);
        return grader.parseGradingConfigFromJson(configAsJson);
    }

    private Map<String, File> generateSourceFiles(File runnerDir) throws IOException {
        File sourceDir = new File(runnerDir, "source");
        FileUtils.forceMkdir(sourceDir);

        ImmutableMap.Builder<String, File> sourceFiles = ImmutableMap.builder();

        for (Map.Entry<String, SourceFile> entry : request.getSourceFiles().entrySet()) {
            File file = new File(sourceDir, entry.getValue().getName());
            FileUtils.writeByteArrayToFile(file, entry.getValue().getContent());

            sourceFiles.put(entry.getKey(), file);
        }
        return sourceFiles.build();
    }

    private Map<String, File> listFilesAsMap(File dir) throws FileNotFoundException {
        File[] files = dir.listFiles();

        if (files == null) {
            throw new FileNotFoundException(dir.getAbsolutePath() + " not found");
        }

        return Arrays.asList(files).stream().collect(Collectors.toMap(e -> e.getName(), e -> e));
    }
}
