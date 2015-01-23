package org.iatoki.judgels.gabriel.blackbox;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.iatoki.judgels.gabriel.FakeClientMessage;
import org.iatoki.judgels.gabriel.FakeSealtiel;
import org.iatoki.judgels.gabriel.GabrielConfig;
import org.iatoki.judgels.gabriel.GraderRegistry;
import org.iatoki.judgels.gabriel.GradingException;
import org.iatoki.judgels.gabriel.GradingRunner;
import org.iatoki.judgels.gabriel.Language;
import org.iatoki.judgels.gabriel.LanguageRegistry;
import org.iatoki.judgels.gabriel.SandboxProvider;
import org.iatoki.judgels.gabriel.sandboxes.FakeSandboxProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public final class BlackBoxGradingRunner implements GradingRunner {
    private final String senderChannel;
    private final BlackBoxGradingRequest request;
    private final FakeSealtiel sealtiel;

    public BlackBoxGradingRunner(String senderChannel, BlackBoxGradingRequest request, FakeSealtiel sealtiel) {
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
            try {
                BlackBoxGrader grader = (BlackBoxGrader) GraderRegistry.getInstance().getGrader(request.getGradingType());
                Language language = LanguageRegistry.getInstance().getLanguage(request.getGradingLanguage());

                File runnerDir = getRunnerDir();
                Map<String, File> sourceFiles = generateSourceFiles(runnerDir);
                SandboxProvider sandboxProvider = getSandboxProvider(runnerDir);
                File tempDir = getTempDir(runnerDir);

                File problemGradingDir = getProblemGradingDir(request.getProblemJid(), request.getProblemLastUpdate());
                Map<String, File> helperFiles = generateHelperFiles(problemGradingDir);
                Map<String, File> testDataFiles = generateTestDataFiles(problemGradingDir);
                BlackBoxGradingConfig config = parseGradingConfig(problemGradingDir, grader);

                result = grader.grade(sandboxProvider, tempDir, language, sourceFiles, helperFiles, testDataFiles, config);
            } catch (IOException | IllegalArgumentException e) {
                throw new InitializationException(e.getMessage());
            }
        } catch (CompilationException e) {
            result = BlackBoxGradingResult.compileError(e.getMessage());
        } catch (GradingException e) {
            System.out.println("Grading id " + getId() + " error : " + e.getMessage());

            result = BlackBoxGradingResult.internalError();
        }

        BlackBoxGradingResponse response = new BlackBoxGradingResponse(request.getSubmissionJid(), result);
        FakeClientMessage message = new FakeClientMessage(senderChannel, "BlackBoxGradingResponse", new Gson().toJson(response));

        sealtiel.sendMessage(message);
    }

    private File getRunnerDir() throws IOException {
        File runnerDir = new File(GabrielConfig.getInstance().getTempDir(), getId());
        FileUtils.forceMkdir(runnerDir);
        return runnerDir;
    }

    private SandboxProvider getSandboxProvider(File runnerDir) throws IOException {
        File sandboxesDir = new File(runnerDir, "sandboxes");
        FileUtils.forceMkdir(sandboxesDir);

        return new FakeSandboxProvider(sandboxesDir);
    }

    private File getTempDir(File runnerDir) throws IOException {
        File tempDir = new File(runnerDir, "temp");
        FileUtils.forceMkdir(tempDir);

        return tempDir;
    }

    private File getProblemGradingDir(String problemJid, long problemLastUpdate) throws IOException {
        return new File(GabrielConfig.getInstance().getProblemDir(), problemJid);
    }

    private Map<String, File> generateHelperFiles(File problemGradingDir) throws FileNotFoundException {
        File helperDir = new File(problemGradingDir, "helpers");
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

        for (Map.Entry<String, byte[]> entry : request.getSourceFiles().entrySet()) {
            File sourceFile = new File(sourceDir, entry.getKey());
            FileUtils.writeByteArrayToFile(sourceFile, entry.getValue());

            sourceFiles.put(entry.getKey(), sourceFile);
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
