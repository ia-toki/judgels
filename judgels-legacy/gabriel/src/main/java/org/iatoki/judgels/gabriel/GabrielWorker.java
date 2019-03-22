package org.iatoki.judgels.gabriel;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.palantir.conjure.java.api.errors.RemoteException;
import judgels.gabriel.api.GradingConfig;
import judgels.sealtiel.api.message.MessageData;
import judgels.sealtiel.api.message.MessageService;
import judgels.service.api.client.BasicAuthHeader;
import org.apache.commons.io.FileUtils;
import org.iatoki.judgels.api.JudgelsAPIClientException;
import org.iatoki.judgels.api.sandalphon.SandalphonClientAPI;
import org.iatoki.judgels.api.sandalphon.SandalphonProgrammingProblemInfo;
import org.iatoki.judgels.gabriel.sandboxes.SandboxFactory;
import org.iatoki.judgels.gabriel.sandboxes.impls.FakeSandboxFactory;
import org.iatoki.judgels.gabriel.sandboxes.impls.MoeIsolateSandboxFactory;
import org.slf4j.MDC;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class GabrielWorker implements Runnable {

    private final String senderChannel;
    private final GradingRequest request;
    private final BasicAuthHeader sealtielClientAuthHeader;
    private final MessageService messageService;
    private final SandalphonClientAPI sandalphonClientAPI;
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

    public GabrielWorker(String senderChannel, GradingRequest request, BasicAuthHeader sealtielClientAuthHeader, MessageService messageService, SandalphonClientAPI sandalphonClientAPI, long messageId) {
        this.senderChannel = senderChannel;
        this.request = request;
        this.sealtielClientAuthHeader = sealtielClientAuthHeader;
        this.messageService = messageService;
        this.sandalphonClientAPI = sandalphonClientAPI;
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
            MessageData message = new MessageData.Builder()
                    .targetJid(senderChannel)
                    .type("GradingResponse")
                    .content(new Gson().toJson(response))
                    .build();
            messageService.sendMessage(sealtielClientAuthHeader, message);
            messageService.confirmMessage(sealtielClientAuthHeader, messageId);
        } catch (RemoteException e) {
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
        File problemGradingDir;

        if (GabrielProperties.getInstance().getSandalphonLocalBaseDataDir() != null) {
            problemGradingDir = FileUtils.getFile(GabrielProperties.getInstance().getSandalphonLocalBaseDataDir(), "problems", problemJid, "grading");
        } else {
            problemGradingDir = new File(GabrielProperties.getInstance().getProblemDir(), problemJid);

            if (mustFetchProblemGradingFiles(problemJid, problemGradingDir)) {
                fetchProblemGradingFiles(problemJid, problemGradingDir);
            }

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

            SandalphonProgrammingProblemInfo sandalphonProgrammingProblemInfo;

            try {
                sandalphonProgrammingProblemInfo = sandalphonClientAPI.getProgrammingProblemInfo(problemJid);
            } catch (JudgelsAPIClientException e) {
                System.out.println(e.toString());
                System.out.println(e.getClass());
                throw new InitializationException("Cannot fetch problem info");
            }

            long gradingLastUpdateTime = sandalphonProgrammingProblemInfo.getGradingLastUpdateTime().getTime();

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


            InputStream gradingFiles;
            try {
                gradingFiles = sandalphonClientAPI.downloadProgrammingProblemGradingFiles(problemJid);
            } catch (JudgelsAPIClientException e) {
                throw new InitializationException("Cannot fetch problem grading files");
            }

            byte[] buffer = new byte[4096];
            ZipInputStream zis = new ZipInputStream(gradingFiles);
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
