package org.iatoki.judgels.gabriel.blackbox;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.iatoki.judgels.gabriel.*;
import org.iatoki.judgels.gabriel.blackbox.sandboxes.FakeSandboxFactory;
import org.iatoki.judgels.gabriel.blackbox.sandboxes.MoeIsolateSandboxFactory;
import org.iatoki.judgels.sealtiel.client.ClientMessage;
import org.iatoki.judgels.sealtiel.client.Sealtiel;
import org.slf4j.MDC;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class BlackBoxGradingWorker implements GradingWorker {
    private final String senderChannel;
    private final BlackBoxGradingRequest request;
    private final Sealtiel sealtiel;

    private final long messageId;

    private File engineDir;
    private BlackBoxGradingEngine engine;
    private BlackBoxGradingConfig config;
    private BlackBoxGradingSource source;
    private GradingLanguage language;
    private SandboxFactory sandboxFactory;
    private Map<String, File> sourceFiles;
    private Map<String, File> helperFiles;
    private Map<String, File> testDataFiles;

    public BlackBoxGradingWorker(String senderChannel, BlackBoxGradingRequest request, Sealtiel sealtiel, long messageId) {
        this.senderChannel = senderChannel;
        this.request = request;
        this.sealtiel = sealtiel;
        this.messageId = messageId;
    }

    @Override
    public void run() {
        MDC.put("gradingJID", request.getGradingJid());

        GabrielLogger.getLogger().info("Grading worker started.");

        BlackBoxGradingResult result;
        // TODO extend timeout
        try {
            GabrielLogger.getLogger().info("Grading started.");

            MDC.put("phase", "Initialization");

            GabrielLogger.getLogger().info("Initialization started.");
            initialize();
            GabrielLogger.getLogger().info("Initialization finished.");

            result = engine.gradeAfterInitialization(sandboxFactory, engineDir, language, sourceFiles, helperFiles, testDataFiles, config);

            MDC.remove("phase");

            GabrielLogger.getLogger().info("Grading done. Result: {} {}", result.getVerdict().getCode(), result.getScore());

        } catch (Exception e) {
            GabrielLogger.getLogger().error("Grading failed!", e);
            GabrielLogger.getLogger().error("Message:", e.getMessage());
            result = BlackBoxGradingResult.internalErrorResult();
        }

        MDC.put("phase", "Cleanup");

        GabrielLogger.getLogger().info("Cleanup started.");
        engine.cleanUp();
        GabrielLogger.getLogger().info("Cleanup finished.");

        MDC.remove("phase");

        GabrielLogger.getLogger().info("Sending grading response started.");
        BlackBoxGradingResponse response = new BlackBoxGradingResponse(request.getGradingJid(), result);
        try {
            ClientMessage message = new ClientMessage(senderChannel, "BlackBoxGradingResponse", new Gson().toJson(response));

            sealtiel.sendMessage(message);
            sealtiel.sendConfirmation(messageId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        GabrielLogger.getLogger().info("Sending grading response finished.");

        GabrielLogger.getLogger().info("Grading worker finished.");
    }

    private void initialize() throws InitializationException {
        source = (BlackBoxGradingSource) request.getGradingSource();

        try {
            engine = (BlackBoxGradingEngine) GradingEngineRegistry.getInstance().getEngine(request.getGradingEngine());
            language = GradingLanguageRegistry.getInstance().getLanguage(request.getGradingLanguage());

            GabrielUtils.getGradingReadLock().lock();
            File workerDir = getWorkerDir();
            sourceFiles = generateSourceFiles(workerDir);
            sandboxFactory = getSandboxProvider(workerDir);
            engineDir = getEngineDir(workerDir);
            GabrielUtils.getGradingReadLock().unlock();

            File problemGradingDir = getProblemGradingDir(request.getProblemJid());
            helperFiles = generateHelperFiles(problemGradingDir);
            testDataFiles = generateTestDataFiles(problemGradingDir);
            config = parseGradingConfig(problemGradingDir, engine);
        } catch (IOException | IllegalArgumentException e) {
            throw new InitializationException(e.getMessage());
        }
    }

    private File getWorkerDir() throws IOException {
        File runnerDir = new File(GabrielProperties.getInstance().getWorkerDir(), request.getGradingJid());
        FileUtils.forceMkdir(runnerDir);
        return runnerDir;
    }

    private SandboxFactory getSandboxProvider(File workerDir) throws IOException {
        if (GabrielProperties.getInstance().getIsolatePath() != null && GabrielProperties.getInstance().getIwrapperPath() != null) {
            return new MoeIsolateSandboxFactory(GabrielProperties.getInstance().getIsolatePath(), GabrielProperties.getInstance().getIwrapperPath());
        } else {
            File sandboxesDir = new File(workerDir, "sandbox");
            FileUtils.forceMkdir(sandboxesDir);
            return new FakeSandboxFactory(sandboxesDir);
        }
    }

    private File getEngineDir(File workerDir) throws IOException {
        File tempDir = new File(workerDir, "engine");
        FileUtils.forceMkdir(tempDir);

        return tempDir;
    }

    private File getProblemGradingDir(String problemJid) throws InitializationException, IOException {
        File problemGradingDir = new File(GabrielProperties.getInstance().getProblemDir(), problemJid);

        GabrielUtils.getGradingFetchCheckLock().lock();
        if (mustFetchProblemGradingFiles(problemJid, problemGradingDir)) {
            GabrielUtils.getGradingWriteLock().lock();
            fetchProblemGradingFiles(problemJid, problemGradingDir);
            GabrielUtils.getGradingWriteLock().unlock();
        }
        GabrielUtils.getGradingFetchCheckLock().unlock();

        return problemGradingDir;
    }

    private boolean mustFetchProblemGradingFiles(String problemJid, File problemGradingDir) throws InitializationException, IOException {
        if (!problemGradingDir.exists()) {
            GabrielLogger.getLogger().info("Problem grading files cache not found. Must fetch grading files.");
            return true;
        }

        File gradingLastUpdateTimeFile = new File(problemGradingDir, "lastUpdateTime.txt");
        if (!gradingLastUpdateTimeFile.exists()) {
            GabrielLogger.getLogger().info("{} not found. Must fetch grading files.", gradingLastUpdateTimeFile.getAbsolutePath());
            return true;
        }

        long cachedGradingLastUpdateTime;

        try {
            cachedGradingLastUpdateTime = Long.parseLong(FileUtils.readFileToString(gradingLastUpdateTimeFile));
        } catch (IOException e) {
            GabrielLogger.getLogger().info("Cannot parse {}. Must fetch grading files.", gradingLastUpdateTimeFile.getAbsolutePath());
            return true;
        }

        HttpPost post = GabrielProperties.getInstance().getGetGradingLastUpdateTimeRequest(problemJid);
        HttpClient client = HttpClientBuilder.create().build();

        HttpResponse response = client.execute(post);

        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            throw new InitializationException("Cannot fetch problem grading files");
        }

        long gradingLastUpdateTime = Long.parseLong(IOUtils.toString(response.getEntity().getContent()));

        if (gradingLastUpdateTime != cachedGradingLastUpdateTime) {
            GabrielLogger.getLogger().info("Problem grading last update time = {}, whereas the cached one = {}. Must fetch grading files.", gradingLastUpdateTime, cachedGradingLastUpdateTime);
            return true;
        }
        return false;
    }

    private void fetchProblemGradingFiles(String problemJid, File problemGradingDir) throws InitializationException, IOException  {
        GabrielLogger.getLogger().info("Fetching test data files from Sandalphon started.");
        FileUtils.deleteDirectory(problemGradingDir);
        FileUtils.forceMkdir(problemGradingDir);

        HttpPost post = GabrielProperties.getInstance().getFetchProblemGradingFilesRequest(problemJid);
        HttpClient client = HttpClientBuilder.create().build();

        HttpResponse response = client.execute(post);

        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            throw new InitializationException("Cannot fetch problem grading files");
        }

        byte[] buffer = new byte[4096];
        ZipInputStream zis = new ZipInputStream(response.getEntity().getContent());
        ZipEntry ze = zis.getNextEntry();
        while (ze!=null) {
            String filename = ze.getName();
            File file = new File(problemGradingDir, filename);
            FileUtils.forceMkdir(file.getParentFile());

            FileOutputStream fos = new FileOutputStream(file);

            int len;
            while ((len = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }

            fos.close();
            ze = zis.getNextEntry();
        }

        zis.closeEntry();
        zis.close();

        FileUtils.forceMkdir(new File(problemGradingDir, "helpers"));
        FileUtils.forceMkdir(new File(problemGradingDir, "testdata"));

        GabrielLogger.getLogger().info("Fetching test data files from Sandalphon finished.");
    }

    private Map<String, File> generateHelperFiles(File problemGradingDir) throws FileNotFoundException {
        File helperDir = new File(problemGradingDir, "helpers");
        return listFilesAsMap(helperDir);
    }

    private Map<String, File> generateTestDataFiles(File problemGradingDir) throws FileNotFoundException {
        File testDatDir = new File(problemGradingDir, "testdata");
        return listFilesAsMap(testDatDir);
    }

    private BlackBoxGradingConfig parseGradingConfig(File problemGradirDir, BlackBoxGradingEngine engine) throws IOException {
        File config = new File(problemGradirDir, "config.json");
        String configAsJson = FileUtils.readFileToString(config);
        return (BlackBoxGradingConfig) engine.createGradingConfigFromJson(configAsJson);
    }

    private Map<String, File> generateSourceFiles(File runnerDir) throws IOException {
        File sourceDir = new File(runnerDir, "source");
        FileUtils.forceMkdir(sourceDir);

        ImmutableMap.Builder<String, File> sourceFiles = ImmutableMap.builder();

        for (Map.Entry<String, SourceFile> entry : source.getSourceFiles().entrySet()) {
            File file = new File(sourceDir, entry.getValue().getName());

            FileUtils.writeStringToFile(file, entry.getValue().getContent());
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
