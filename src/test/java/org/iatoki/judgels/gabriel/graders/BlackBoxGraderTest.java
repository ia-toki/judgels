package org.iatoki.judgels.gabriel.graders;

import com.beust.jcommander.internal.Maps;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;
import org.apache.commons.io.FileUtils;
import org.iatoki.judgels.gabriel.GradingException;
import org.iatoki.judgels.gabriel.Language;
import org.iatoki.judgels.gabriel.SandboxExecutionStatus;
import org.iatoki.judgels.gabriel.SandboxFactory;
import org.iatoki.judgels.gabriel.blackbox.BlackBoxGrader;
import org.iatoki.judgels.gabriel.blackbox.BlackBoxGradingConfig;
import org.iatoki.judgels.gabriel.blackbox.BlackBoxGradingResult;
import org.iatoki.judgels.gabriel.languages.CppLanguage;
import org.iatoki.judgels.gabriel.sandboxes.FakeSandboxFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class BlackBoxGraderTest {

    private ImmutableList.Builder<List<SandboxExecutionStatus>> arrangedStatusesListBuilder;

    private Language language;

    private File workerDir;

    private File sourceDir;
    private File graderDir;
    private File sandboxDir;

    private Map<String, File> sourceFiles;

    private final Map<String, File> testDataFiles;
    private final Map<String, File> helperFiles;

    protected BlackBoxGraderTest(String resourceDirname) {
        File resourceDir = new File(this.getClass().getClassLoader().getResource(resourceDirname).getPath());

        this.sourceDir = new File(resourceDir, "source");

        File testDataDir = new File(resourceDir, "testdata");
        File helperDir = new File(resourceDir, "helper");

        this.testDataFiles = listFilesAsMap(testDataDir);
        this.helperFiles = listFilesAsMap(helperDir);

        this.language = new CppLanguage("/usr/bin/g++");
    }

    @BeforeMethod
    public void setUp() {
        arrangedStatusesListBuilder = ImmutableList.builder();

        workerDir = Files.createTempDir();

        graderDir = new File(workerDir, "grader");
        sandboxDir = new File(workerDir, "sandbox");

        try {
            FileUtils.forceMkdir(graderDir);
            FileUtils.forceMkdir(sandboxDir);
        } catch (IOException e) {
            throw new RuntimeException("Cannot create temp directories");
        }

        sourceFiles = Maps.newHashMap();
    }

    @AfterMethod
    public void tearDown() {
        try {
            FileUtils.forceDelete(workerDir);
        } catch (IOException e) {
            throw new RuntimeException("Cannot delete temp directories");
        }
    }

    protected final void addSourceFile(String key, String filename) {
        sourceFiles.put(key, new File(sourceDir, filename));
    }

    protected final void addFakeSandbox(List<SandboxExecutionStatus> arrangedStatuses) {
        arrangedStatusesListBuilder.add(arrangedStatuses);
    }

    protected final BlackBoxGradingResult runGrader(BlackBoxGrader grader, BlackBoxGradingConfig config) throws GradingException {
        SandboxFactory sandboxFactory = new FakeSandboxFactory(sandboxDir, arrangedStatusesListBuilder.build());
        return grader.gradeAfterInitialization(sandboxFactory, graderDir, language, sourceFiles, helperFiles, testDataFiles, config);
    }

    private Map<String, File> listFilesAsMap(File dir) {
        File[] files = dir.listFiles();
        if (files == null) {
            return ImmutableMap.of();
        }
        return Arrays.asList(files).stream().collect(Collectors.toMap(e -> e.getName(), e -> e));
    }
}
