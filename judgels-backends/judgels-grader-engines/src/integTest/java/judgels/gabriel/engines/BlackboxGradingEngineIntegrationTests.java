package judgels.gabriel.engines;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import judgels.JudgelsObjectMappers;
import judgels.gabriel.api.GradingConfig;
import judgels.gabriel.api.GradingEngine;
import judgels.gabriel.api.GradingException;
import judgels.gabriel.api.GradingLanguage;
import judgels.gabriel.api.GradingResult;
import judgels.gabriel.api.GradingResultDetails;
import judgels.gabriel.api.GradingSource;
import judgels.gabriel.api.SandboxExecutionResult;
import judgels.gabriel.api.SandboxExecutionStatus;
import judgels.gabriel.api.SandboxFactory;
import judgels.gabriel.api.SubtaskResult;
import judgels.gabriel.api.TestCaseResult;
import judgels.gabriel.api.TestGroupResult;
import judgels.gabriel.api.Verdict;
import judgels.gabriel.languages.cpp.Cpp17GradingLanguage;
import judgels.gabriel.sandboxes.fake.FakeSandboxFactory;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public class BlackboxGradingEngineIntegrationTests {
    protected static final ObjectMapper MAPPER = JudgelsObjectMappers.OBJECT_MAPPER;

    private GradingEngine engine;
    private GradingLanguage language;

    private File workerDir;

    private File resourceDir;
    private File sourceDir;
    private File gradingDir;
    private File sandboxDir;

    private Map<String, File> sourceFiles;

    private final Map<String, File> testDataFiles;
    private final Map<String, File> helperFiles;

    protected BlackboxGradingEngineIntegrationTests(String resourceDirname, GradingEngine engine) {
        URL url = Resources.getResource("engines/" + resourceDirname);
        File resourceDir = new File(url.getPath());

        this.resourceDir = resourceDir;
        this.sourceDir = new File(resourceDir, "source");

        File testDataDir = new File(resourceDir, "testdata");
        File helperDir = new File(resourceDir, "helper");

        this.testDataFiles = listFilesAsMap(testDataDir);
        this.helperFiles = listFilesAsMap(helperDir);

        this.engine = engine;
        this.language = new Cpp17GradingLanguage();
    }

    @BeforeEach
    public void before() throws IOException {
        workerDir = Files.createTempDir();

        gradingDir = new File(workerDir, "grading");
        sandboxDir = new File(workerDir, "sandbox");

        FileUtils.forceMkdir(gradingDir);
        FileUtils.forceMkdir(sandboxDir);

        sourceFiles = Maps.newHashMap();
    }

    @AfterEach
    public void after() throws IOException {
        FileUtils.forceDelete(workerDir);
    }

    protected void setCustomSources(GradingLanguage language, String sourceDirSuffix) {
        this.language = language;
        this.sourceDir = new File(resourceDir, "source-" + sourceDirSuffix);
    }

    protected void addSourceFile(String key, String filename) {
        sourceFiles.put(key, new File(sourceDir, filename));
    }

    protected GradingResult runEngine(GradingConfig config) throws GradingException {
        SandboxFactory sandboxFactory = new FakeSandboxFactory(sandboxDir);
        GradingSource source = new GradingSource.Builder()
                .sourceFiles(sourceFiles)
                .testDataFiles(testDataFiles)
                .helperFiles(helperFiles)
                .build();
        return engine.grade(gradingDir, config, language, source, sandboxFactory);
    }

    protected GradingResultDetails getDetails(GradingResult result) {
        try {
            return MAPPER.readValue(result.getDetails(), GradingResultDetails.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void assertResult(
            GradingConfig config,
            Verdict verdict,
            int score,
            List<TestGroupResult> testDataResults,
            List<SubtaskResult> subtaskResults) throws GradingException {

        GradingResult result = runEngine(config);
        GradingResultDetails details = getDetails(result);

        assertThat(result.getVerdict()).isEqualTo(verdict);
        assertThat(result.getScore()).isEqualTo(score);
        assertThat(details.getTestDataResults()).isEqualTo(testDataResults);
        assertThat(details.getSubtaskResults()).isEqualTo(subtaskResults);
    }

    protected TestGroupResult testGroupResult(int id, TestCaseResult... testCaseResults) {
        return new TestGroupResult.Builder().id(id).addTestCaseResults(testCaseResults).build();
    }

    protected TestCaseResult testCaseResult(Verdict verdict, String score, int... subtaskIds) {
        return testCaseResult(verdict, score, Optional.of(SandboxExecutionStatus.ZERO_EXIT_CODE), subtaskIds);
    }

    protected TestCaseResult testCaseResult(
            Verdict verdict,
            String score,
            Optional<SandboxExecutionStatus> status,
            int... subtaskIds) {
        Optional<SandboxExecutionResult> result = status.map(s -> new SandboxExecutionResult.Builder()
                .time(100)
                .memory(1000)
                .status(s)
                .message("OK")
                .build());
        return new TestCaseResult.Builder()
                .verdict(verdict)
                .score(score)
                .addSubtaskIds(subtaskIds)
                .executionResult(result)
                .build();
    }

    protected SubtaskResult subtaskResult(int id, Verdict verdict, double score) {
        return new SubtaskResult.Builder().id(id).verdict(verdict).score(score).build();
    }

    private Map<String, File> listFilesAsMap(File dir) {
        File[] files = dir.listFiles();
        if (files == null) {
            return ImmutableMap.of();
        }
        return Arrays.asList(files).stream().collect(Collectors.toMap(e -> e.getName(), e -> e));
    }
}
