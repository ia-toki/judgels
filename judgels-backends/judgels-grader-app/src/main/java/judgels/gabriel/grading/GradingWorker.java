package judgels.gabriel.grading;

import static java.util.stream.Collectors.toMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Named;
import judgels.JudgelsObjectMappers;
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
import judgels.gabriel.cache.ProblemCache;
import judgels.gabriel.engines.GradingEngineRegistry;
import judgels.gabriel.languages.GradingLanguageRegistry;
import judgels.gabriel.sandboxes.fake.FakeSandboxFactory;
import judgels.gabriel.sandboxes.isolate.IsolateSandboxFactory;
import judgels.messaging.MessageClient;
import judgels.messaging.api.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class GradingWorker {
    private static final ObjectMapper MAPPER = JudgelsObjectMappers.OBJECT_MAPPER;
    private static final Logger LOGGER = LoggerFactory.getLogger(GradingWorker.class);

    private final GradingConfiguration gradingConfig;
    private final Path workersDir;
    private final ProblemCache problemCache;
    private final MessageClient messageClient;
    private final Optional<IsolateSandboxFactory> isolateSandboxFactory;

    private Message message;
    private GradingRequest request;

    private Path engineDir;
    private Path workerDir;

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
            ProblemCache problemCache,
            MessageClient messageClient,
            Optional<IsolateSandboxFactory> isolateSandboxFactory) {

        this.gradingConfig = gradingConfig;
        this.workersDir = workersDir;
        this.problemCache = problemCache;
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
            language = GradingLanguageRegistry.getInstance().get(request.getGradingLanguage());
            workerDir = getWorkerDir();
            engineDir = getEngineDir(workerDir);
            sourceFiles = generateSourceFiles(workerDir);
            sandboxFactory = getSandboxFactory(workerDir);

            Path problemGradingDir = problemCache.getProblemGradingDir(request.getProblemJid(), request.getGradingLastUpdateTime());

            helperFiles = generateHelperFiles(problemGradingDir);
            testDataFiles = generateTestDataFiles(problemGradingDir);

            engine = getGradingEngine(problemGradingDir);
            config = parseGradingConfig(problemGradingDir, engine);
        } catch (IOException | IllegalArgumentException | InterruptedException e) {
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
        result = engine.grade(engineDir.toFile(), config, language, source, sandboxFactory);

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
        try (var files = Files.walk(workerDir)) {
            files.sorted(Comparator.reverseOrder()).forEach(f -> f.toFile().delete());
        } catch (IOException e) {
            throw new RuntimeException("Worker finalization failed!", e);
        }

        LOGGER.info("Worker finalization finished.");
    }

    private Path getWorkerDir() throws IOException {
        Path workerDir = workersDir.resolve(request.getGradingJid());
        Files.createDirectories(workerDir);
        return workerDir;
    }

    private Path getEngineDir(Path workerDir) throws IOException {
        Path engineDir = workerDir.resolve("engine");
        Files.createDirectories(engineDir);
        return engineDir;
    }

    private Map<String, File> generateSourceFiles(Path workerDir) throws IOException {
        Path sourceDir = workerDir.resolve("source");
        Files.createDirectories(sourceDir);

        ImmutableMap.Builder<String, File> sourceFilesBuilder = ImmutableMap.builder();

        for (Map.Entry<String, SourceFile> entry : source.getSubmissionFiles().entrySet()) {
            Path file = sourceDir.resolve(entry.getValue().getName());

            Files.write(file, entry.getValue().getContent());
            sourceFilesBuilder.put(entry.getKey(), file.toFile());
        }
        return sourceFilesBuilder.build();
    }

    private SandboxFactory getSandboxFactory(Path workerDir) throws IOException {
        if (isolateSandboxFactory.isPresent()) {
            return isolateSandboxFactory.get();
        } else {
            Path sandboxesDir = workerDir.resolve("sandboxes");
            Files.createDirectories(sandboxesDir);
            return new FakeSandboxFactory(sandboxesDir.toFile());
        }
    }

    private Map<String, File> generateHelperFiles(Path problemGradingDir) throws IOException {
        Path helperDir = problemGradingDir.resolve("helpers");
        return listFilesAsMap(helperDir);
    }

    private Map<String, File> generateTestDataFiles(Path problemGradingDir) throws IOException {
        Path testDataDir = problemGradingDir.resolve("testdata");
        return listFilesAsMap(testDataDir);
    }

    private GradingEngine getGradingEngine(Path problemGradingDir) throws IOException {
        Path engineFile = problemGradingDir.resolve("engine.txt");
        String engineString = Files.readString(engineFile, StandardCharsets.UTF_8);
        return GradingEngineRegistry.getInstance().get(engineString);
    }

    private GradingConfig parseGradingConfig(Path problemGradingDir, GradingEngine engine) throws IOException {
        Path gradingConfig = problemGradingDir.resolve("config.json");
        String configAsJson = Files.readString(gradingConfig, StandardCharsets.UTF_8);
        return engine.parseConfig(MAPPER, configAsJson);
    }

    private Map<String, File> listFilesAsMap(Path dir) throws IOException {
        try (var files = Files.list(dir)) {
            return files.map(Path::toFile).collect(toMap(File::getName, f -> f));
        }
    }
}
