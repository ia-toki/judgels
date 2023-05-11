package judgels.gabriel.grading;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Named;
import judgels.gabriel.api.GabrielObjectMapper;
import judgels.gabriel.api.GradingConfig;
import judgels.gabriel.api.GradingEngine;
import judgels.gabriel.api.GradingException;
import judgels.gabriel.api.GradingLanguage;
import judgels.gabriel.api.GradingRequest;
import judgels.gabriel.api.GradingResponse;
import judgels.gabriel.api.GradingResult;
import judgels.gabriel.api.GradingSource;
import judgels.gabriel.api.SandboxFactory;
import judgels.gabriel.api.SourceFile;
import judgels.gabriel.api.SubmissionSource;
import judgels.gabriel.api.Verdict;
import judgels.gabriel.engines.GradingEngineRegistry;
import judgels.gabriel.languages.GradingLanguageRegistry;
import judgels.gabriel.sandboxes.fake.FakeSandboxFactory;
import judgels.gabriel.sandboxes.isolate.IsolateSandboxFactory;
import judgels.messaging.MessageClient;
import judgels.messaging.api.Message;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class GradingWorker {
    private static final ObjectMapper MAPPER = GabrielObjectMapper.getInstance();
    private static final Logger LOGGER = LoggerFactory.getLogger(GradingWorker.class);

    private final GradingConfiguration gradingConfig;
    private final Path workersDir;
    private final Path problemsDir;
    private final MessageClient messageClient;
    private final Optional<IsolateSandboxFactory> isolateSandboxFactory;

    private Message message;
    private GradingRequest request;

    private File engineDir;
    private File workerDir;

    private GradingEngine engine;
    private GradingConfig config;
    private GradingLanguage language;
    private SubmissionSource source;

    private SandboxFactory sandboxFactory;

    private Map<String, File> sourceFiles;
    private Map<String, File> helperFiles;
    private Map<String, File> testDataFiles;

    private GradingResult result;

    @Inject
    public GradingWorker(
            GradingConfiguration gradingConfig,
            @Named("workersDir") Path workersDir,
            @Named("problemsDir") Path problemsDir,
            MessageClient messageClient,
            Optional<IsolateSandboxFactory> isolateSandboxFactory) {

        this.gradingConfig = gradingConfig;
        this.workersDir = workersDir;
        this.problemsDir = problemsDir;
        this.messageClient = messageClient;
        this.isolateSandboxFactory = isolateSandboxFactory;
    }

    public void process(Message message) {
        this.message = message;
        try {
            request = MAPPER.readValue(message.getContent(), GradingRequest.class);
        } catch (IOException e) {
            LOGGER.error("Parsing grading request failed!", e);
            return;
        }

        MDC.put("gradingJID", request.getGradingJid());
        try {
            initializeWorker();
            grade();
        } catch (Exception e) {
            LOGGER.error("Grading failed!", e);
            result = new GradingResult.Builder()
                    .verdict(Verdict.INTERNAL_ERROR)
                    .score(0)
                    .details(e.toString())
                    .build();
        }

        try {
            respond();
            finalizeWorker();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        MDC.clear();
    }


    private void initializeWorker() {
        LOGGER.info("Worker initialization started.");

        source = request.getSubmissionSource();

        try {
            engine = GradingEngineRegistry.getInstance().get(request.getGradingEngine());
            language = GradingLanguageRegistry.getInstance().get(request.getGradingLanguage());
            workerDir = getWorkerDir();
            sourceFiles = generateSourceFiles(workerDir);
            sandboxFactory = getSandboxFactory(workerDir);
            engineDir = getEngineDir(workerDir);

            File problemGradingDir = getProblemGradingDir(request.getProblemJid());
            helperFiles = generateHelperFiles(problemGradingDir);
            testDataFiles = generateTestDataFiles(problemGradingDir);
            config = parseGradingConfig(problemGradingDir, engine);
        } catch (IOException | IllegalArgumentException e) {
            throw new RuntimeException("Worker initialization failed!", e);
        }

        LOGGER.info("Worker initialization finished.");
    }

    private void grade() throws GradingException {
        GradingSource source = new GradingSource.Builder()
                .sourceFiles(sourceFiles)
                .testDataFiles(testDataFiles)
                .helperFiles(helperFiles)
                .build();
        result = engine.grade(engineDir, config, language, source, sandboxFactory);

        LOGGER.info("Grading finished. Result: {} {}", result.getVerdict().getCode(), result.getScore());
    }

    private void respond() {
        LOGGER.info("Grading result ready to send.");
        GradingResponse response = new GradingResponse.Builder()
                .gradingJid(request.getGradingJid())
                .result(result)
                .build();

        try {
            messageClient.sendMessage(
                    gradingConfig.getGradingRequestQueueName(),
                    message.getSourceQueueName(),
                    "GradingResponse",
                    MAPPER.writeValueAsString(response));
            messageClient.confirmMessage(message.getId());
        }  catch (IOException e) {
            throw new RuntimeException("Grading result failed to send!", e);
        }

        LOGGER.info("Grading result sent.");
    }

    private void finalizeWorker() {
        LOGGER.info("Worker finalization started.");
        try {
            FileUtils.forceDelete(workerDir);
        } catch (IOException e) {
            throw new RuntimeException("Worker finalization failed!", e);
        }

        LOGGER.info("Worker finalization finished.");
    }


    private File getWorkerDir() throws IOException {
        File dir = new File(workersDir.toFile(), request.getGradingJid());
        FileUtils.forceMkdir(dir);
        return dir;
    }

    private SandboxFactory getSandboxFactory(File workerDir) throws IOException {
        if (isolateSandboxFactory.isPresent()) {
            return isolateSandboxFactory.get();
        } else {
            File sandboxesDir = new File(workerDir, "sandboxes");
            FileUtils.forceMkdir(sandboxesDir);
            return new FakeSandboxFactory(sandboxesDir);
        }
    }

    private File getEngineDir(File workerDir) throws IOException {
        File engineDir = new File(workerDir, "engine");
        FileUtils.forceMkdir(engineDir);
        return engineDir;
    }

    private File getProblemGradingDir(String problemJid) throws IOException {
        File problemGradingDir;

        if (gradingConfig.getLocalSandalphonBaseDataDir().isPresent()) {
            problemGradingDir = Paths.get(
                    gradingConfig.getLocalSandalphonBaseDataDir().get(),
                    "problems",
                    problemJid,
                    "grading").toFile();
        } else {
            problemGradingDir = new File(problemsDir.toFile(), problemJid);
            FileUtils.forceMkdir(problemGradingDir);
        }

        return problemGradingDir;
    }

    private Map<String, File> generateHelperFiles(File problemGradingDir) throws FileNotFoundException {
        File helperDir = new File(problemGradingDir, "helpers");
        return listFilesAsMap(helperDir);
    }

    private Map<String, File> generateTestDataFiles(File problemGradingDir) throws FileNotFoundException {
        File testDatDir = new File(problemGradingDir, "testdata");
        return listFilesAsMap(testDatDir);
    }

    private GradingConfig parseGradingConfig(File problemGradingDir, GradingEngine engine) throws IOException {
        File gradingConfig = new File(problemGradingDir, "config.json");
        String configAsJson = FileUtils.readFileToString(gradingConfig, StandardCharsets.UTF_8);
        return engine.parseConfig(MAPPER, configAsJson);
    }

    private Map<String, File> generateSourceFiles(File runnerDir) throws IOException {
        File sourceDir = new File(runnerDir, "source");
        FileUtils.forceMkdir(sourceDir);

        ImmutableMap.Builder<String, File> sourceFilesBuilder = ImmutableMap.builder();

        for (Map.Entry<String, SourceFile> entry : source.getSubmissionFiles().entrySet()) {
            File file = new File(sourceDir, entry.getValue().getName());

            FileUtils.writeByteArrayToFile(file, entry.getValue().getContent());
            sourceFilesBuilder.put(entry.getKey(), file);
        }
        return sourceFilesBuilder.build();
    }

    private Map<String, File> listFilesAsMap(File dir) throws FileNotFoundException {
        File[] files = dir.listFiles();

        if (files == null) {
            throw new FileNotFoundException(dir.getAbsolutePath() + " not found");
        }

        return Arrays.asList(files).stream().collect(Collectors.toMap(e -> e.getName(), e -> e));
    }
}
