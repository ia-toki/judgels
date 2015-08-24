package org.iatoki.judgels.gabriel;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.iatoki.judgels.gabriel.sandboxes.SandboxFactory;
import org.iatoki.judgels.gabriel.sandboxes.impls.FakeSandboxFactory;
import org.iatoki.judgels.gabriel.sandboxes.impls.MoeIsolateSandboxFactory;
import org.iatoki.judgels.sealtiel.ClientMessage;
import org.iatoki.judgels.sealtiel.Sealtiel;
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

public final class GabrielWorker implements Runnable {

    private final String senderChannel;
    private final GradingRequest request;
    private final Sealtiel sealtiel;
    private final long messageId;

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

    public GabrielWorker(String senderChannel, GradingRequest request, Sealtiel sealtiel, long messageId) {
        this.senderChannel = senderChannel;
        this.request = request;
        this.sealtiel = sealtiel;
        this.messageId = messageId;
    }

    @Override
    public void run() {
        MDC.put("gradingJID", request.getGradingJid());

        GabrielLogger.getLogger().info("Gabriel worker started.");

        // TODO extend timeout

        try {
            initializeWorker();
            gradeRequest();
        } catch (Exception e) {
            GabrielLogger.getLogger().error("Grading failed!", e);
            if (e.getMessage() != null && !e.getMessage().isEmpty()) {
                GabrielLogger.getLogger().error("Message:", e.getMessage());
            }
            result = GradingResult.internalErrorResult(e.toString());
        } finally {
            try {
                respond();
                finalizeWorker();
            } catch (ResponseException | FinalizationException e) {
                throw new RuntimeException(e);
            }
        }

        GabrielLogger.getLogger().info("Grading worker finished.");
    }

    private void initializeWorker() throws InitializationException {
        MDC.put("workerPhase", "INITIALIZE");

        GabrielLogger.getLogger().info("Worker initialization started.");

        source = request.getSubmissionSource();

        try {
            engine = GradingEngineRegistry.getInstance().getEngine(request.getGradingEngine());
            language = GradingLanguageRegistry.getInstance().getLanguage(request.getGradingLanguage());

            workerDir = getWorkerDir();
            sourceFiles = generateSourceFiles(workerDir);
            sandboxFactory = getSandboxProvider(workerDir);
            engineDir = getEngineDir(workerDir);

            File problemGradingDir = getProblemGradingDir(request.getProblemJid());
            helperFiles = generateHelperFiles(problemGradingDir);
            testDataFiles = generateTestDataFiles(problemGradingDir);
            config = parseGradingConfig(problemGradingDir, engine);
        } catch (IOException | IllegalArgumentException e) {
            GabrielLogger.getLogger().error("Worker initialization failed!");
            throw new InitializationException(e);
        }

        GabrielLogger.getLogger().info("Worker initialization finished.");
    }

    private void gradeRequest() throws GradingException {
        MDC.put("workerPhase", "GRADE");
        result = engine.grade(engineDir, config, language, new GradingSource(sourceFiles, testDataFiles, helperFiles), sandboxFactory);

        GabrielLogger.getLogger().info("Grading done. Result: {} {}", result.getVerdict().getCode(), result.getScore());
    }

    private void respond() throws ResponseException {
        MDC.put("workerPhase", "RESPOND");

        GabrielLogger.getLogger().info("Grading result ready to send.");
        GradingResponse response = new GradingResponse(request.getGradingJid(), result);

        try {
            ClientMessage message = new ClientMessage(senderChannel, "GradingResponse", new Gson().toJson(response));

            sealtiel.sendMessage(message);
            sealtiel.sendConfirmation(messageId);

        } catch (IOException e) {
            throw new ResponseException(e);
        }

        GabrielLogger.getLogger().info("Grading result sent.");
    }

    private void finalizeWorker() throws FinalizationException {
        MDC.put("workerPhase", "FINALIZE");

        GabrielLogger.getLogger().info("Worker finalization started.");
        try {
            FileUtils.forceDelete(workerDir);
        } catch (IOException e) {
            GabrielLogger.getLogger().error("Worker finalization failed!");
            throw new FinalizationException(e);
        }

        GabrielLogger.getLogger().info("Worker finalization finished.");
    }

    private File getWorkerDir() throws IOException {
        File dir = new File(GabrielProperties.getInstance().getTempDir(), request.getGradingJid());
        FileUtils.forceMkdir(dir);
        return dir;
    }

    private SandboxFactory getSandboxProvider(File workerDir) throws IOException {
        if (GabrielProperties.getInstance().getMoeIsolatePath() != null && GabrielProperties.getInstance().getMoeIwrapperPath() != null) {
            return new MoeIsolateSandboxFactory(GabrielProperties.getInstance().getMoeIsolatePath(), GabrielProperties.getInstance().getMoeIwrapperPath());
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

        if (mustFetchProblemGradingFiles(problemJid, problemGradingDir)) {
            fetchProblemGradingFiles(problemJid, problemGradingDir);
        }

        return problemGradingDir;
    }

    private boolean mustFetchProblemGradingFiles(String problemJid, File problemGradingDir) throws InitializationException, IOException {
        GabrielUtils.getGradingFetchCheckLock().lock();

        try {
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

            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectionRequestTimeout(30 * 1000)
                    .setSocketTimeout(30 * 1000)
                    .setConnectTimeout(30 * 1000)
                    .build();

            HttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();

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

        } finally {
            GabrielUtils.getGradingFetchCheckLock().unlock();
        }
    }

    private void fetchProblemGradingFiles(String problemJid, File problemGradingDir) throws InitializationException, IOException  {
        GabrielUtils.getGradingWriteLock().lock();

        try {
            GabrielLogger.getLogger().info("Fetching test data files from Sandalphon started.");
            FileUtils.deleteDirectory(problemGradingDir);
            FileUtils.forceMkdir(problemGradingDir);

            HttpPost post = GabrielProperties.getInstance().getFetchProblemGradingFilesRequest(problemJid);

            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectionRequestTimeout(30 * 1000)
                    .setSocketTimeout(30 * 1000)
                    .setConnectTimeout(30 * 1000)
                    .build();

            HttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();

            HttpResponse response = client.execute(post);

            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                throw new InitializationException("Cannot fetch problem grading files");
            }

            byte[] buffer = new byte[4096];
            ZipInputStream zis = new ZipInputStream(response.getEntity().getContent());
            ZipEntry ze = zis.getNextEntry();
            while (ze != null) {
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

        } finally {
            GabrielUtils.getGradingWriteLock().unlock();
        }
    }

    private Map<String, File> generateHelperFiles(File problemGradingDir) throws FileNotFoundException {
        File helperDir = new File(problemGradingDir, "helpers");
        return listFilesAsMap(helperDir);
    }

    private Map<String, File> generateTestDataFiles(File problemGradingDir) throws FileNotFoundException {
        File testDatDir = new File(problemGradingDir, "testdata");
        return listFilesAsMap(testDatDir);
    }

    private GradingConfig parseGradingConfig(File problemGradirDir, GradingEngine engine) throws IOException {
        File gradingConfig = new File(problemGradirDir, "config.json");
        String configAsJson = FileUtils.readFileToString(gradingConfig);
        return engine.createGradingConfigFromJson(configAsJson);
    }

    private Map<String, File> generateSourceFiles(File runnerDir) throws IOException {
        File sourceDir = new File(runnerDir, "source");
        FileUtils.forceMkdir(sourceDir);

        ImmutableMap.Builder<String, File> sourceFilesBuilder = ImmutableMap.builder();

        for (Map.Entry<String, SourceFile> entry : source.getSubmissionFiles().entrySet()) {
            File file = new File(sourceDir, entry.getValue().getName());

            FileUtils.writeStringToFile(file, entry.getValue().getContent());
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
