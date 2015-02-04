package org.iatoki.judgels.gabriel.blackbox;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.iatoki.judgels.gabriel.FakeClientMessage;
import org.iatoki.judgels.gabriel.FakeSealtiel;
import org.iatoki.judgels.gabriel.GabrielProperties;
import org.iatoki.judgels.gabriel.GradingEngineRegistry;
import org.iatoki.judgels.gabriel.GradingException;
import org.iatoki.judgels.gabriel.GradingWorker;
import org.iatoki.judgels.gabriel.GradingLanguage;
import org.iatoki.judgels.gabriel.GradingLanguageRegistry;
import org.iatoki.judgels.gabriel.blackbox.sandboxes.FakeSandboxFactory;

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
    private final FakeSealtiel sealtiel;

    private File engineDir;
    private BlackBoxGradingEngine engine;
    private BlackBoxGradingConfig config;
    private BlackBoxGradingSource source;
    private GradingLanguage language;
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
            result = engine.gradeAfterInitialization(sandboxFactory, engineDir, language, sourceFiles, helperFiles, testDataFiles, config);
        } catch (GradingException e) {
            System.out.println("Grading id " + getId() + " error : " + e.getMessage());
            result = BlackBoxGradingResult.internalErrorResult();
        }

        engine.cleanUp();

        BlackBoxGradingResponse response = new BlackBoxGradingResponse(request.getSubmissionJid(), result);
        FakeClientMessage message = new FakeClientMessage(senderChannel, "BlackBoxGradingResponse", new Gson().toJson(response));

        sealtiel.sendMessage(message);
    }

    private void initialize() throws InitializationException {
        source = (BlackBoxGradingSource) request.getGradingSource();

        try {
            engine = (BlackBoxGradingEngine) GradingEngineRegistry.getInstance().getEngine(request.getGradingEngine());
            language = GradingLanguageRegistry.getInstance().getLanguage(request.getGradingLanguage());

            File workerDir = getWorkerDir();
            sourceFiles = generateSourceFiles(workerDir);
            sandboxFactory = getSandboxProvider(workerDir);
            engineDir = getEngineDir(workerDir);

            File problemGradingDir = getProblemGradingDir(request.getProblemJid(), request.getGradingLastUpdateTime());
            helperFiles = generateHelperFiles(problemGradingDir);
            testDataFiles = generateTestDataFiles(problemGradingDir);
            config = parseGradingConfig(problemGradingDir, engine);
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

        return new FakeSandboxFactory(sandboxesDir);
    }

    private File getEngineDir(File workerDir) throws IOException {
        File tempDir = new File(workerDir, "engine");
        FileUtils.forceMkdir(tempDir);

        return tempDir;
    }

    private File getProblemGradingDir(String problemJid, long problemLastUpdate) throws IOException {
        File problemGradingDir = new File(GabrielProperties.getInstance().getProblemDir(), problemJid);

        if (mustFetchProblemGradingFiles(problemGradingDir, problemLastUpdate)) {
            FileUtils.deleteDirectory(problemGradingDir);
            FileUtils.forceMkdir(problemGradingDir);

            HttpPost post = GabrielProperties.getInstance().getFetchProblemGradingFilesRequest(problemJid);
            HttpClient client = HttpClientBuilder.create().build();

            HttpResponse response = client.execute(post);

            if (response.getStatusLine().getStatusCode() != 200) {
                throw new IOException("Cannot fetch problem grading files");
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

            FileUtils.forceMkdir(new File(problemGradingDir, "helper"));
            FileUtils.forceMkdir(new File(problemGradingDir, "testdata"));
        }

        return problemGradingDir;
    }

    private boolean mustFetchProblemGradingFiles(File problemGradingDir, long gradingLastUpdateTime) {
        if (!problemGradingDir.exists()) {
            return true;
        }

        File gradingLastUpdateTimeFile = new File(problemGradingDir, "lastUpdateTime.txt");
        if (!gradingLastUpdateTimeFile.exists()) {
            return true;
        }

        long cachedGradingLastUpdateTime;

        try {
            cachedGradingLastUpdateTime = Long.parseLong(FileUtils.readFileToString(gradingLastUpdateTimeFile));
        } catch (IOException e) {
            return true;
        }

        return gradingLastUpdateTime != cachedGradingLastUpdateTime;
    }

    private Map<String, File> generateHelperFiles(File problemGradingDir) throws FileNotFoundException {
        File helperDir = new File(problemGradingDir, "helper");
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
