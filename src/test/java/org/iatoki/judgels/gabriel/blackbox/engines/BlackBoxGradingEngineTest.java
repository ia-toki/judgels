package org.iatoki.judgels.gabriel.blackbox.engines;

import com.beust.jcommander.internal.Maps;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;
import org.apache.commons.io.FileUtils;
import org.iatoki.judgels.gabriel.GradingException;
import org.iatoki.judgels.gabriel.GradingLanguage;
import org.iatoki.judgels.gabriel.Verdict;
import org.iatoki.judgels.gabriel.blackbox.BlackBoxGradingConfig;
import org.iatoki.judgels.gabriel.blackbox.BlackBoxGradingEngine;
import org.iatoki.judgels.gabriel.blackbox.BlackBoxGradingResult;
import org.iatoki.judgels.gabriel.sandboxes.FakeSandboxFactory;
import org.iatoki.judgels.gabriel.sandboxes.SandboxFactory;
import org.iatoki.judgels.gabriel.sandboxes.impls.FakeSandboxFactory;
import org.iatoki.judgels.gabriel.blackbox.languages.PlainCppGradingLanguage;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class BlackBoxGradingEngineTest {

    protected static final Verdict VERDICT_CE = new Verdict("CE", "Compilation Error");
    protected static final Verdict VERDICT_OK = new Verdict("OK", "OK");
    protected static final Verdict VERDICT_OK_WORST_WA = new Verdict("OK", "OK (worst: WA)");
    protected static final Verdict VERDICT_OK_WORST_TLE = new Verdict("OK", "OK (worst: TLE)");
    protected static final Verdict VERDICT_OK_WORST_RTE = new Verdict("OK", "OK (worst: RTE)");
    protected static final Verdict VERDICT_AC = new Verdict("AC", "Accepted");
    protected static final Verdict VERDICT_WA = new Verdict("WA", "Wrong Answer");
    protected static final Verdict VERDICT_TLE = new Verdict("TLE", "Time Limit Exceeded");
    protected static final Verdict VERDICT_RTE = new Verdict("RTE", "Runtime Error");

    private GradingLanguage language;

    private File workerDir;

    private File sourceDir;
    private File graderDir;
    private File sandboxDir;

    private Map<String, File> sourceFiles;

    private final Map<String, File> testDataFiles;
    private final Map<String, File> helperFiles;

    protected BlackBoxGradingEngineTest(String resourceDirname) {
        File resourceDir = new File(BlackBoxGradingEngineTest.class.getClassLoader().getResource("blackbox/" + resourceDirname).getPath());

        this.sourceDir = new File(resourceDir, "source");

        File testDataDir = new File(resourceDir, "testdata");
        File helperDir = new File(resourceDir, "helper");

        this.testDataFiles = listFilesAsMap(testDataDir);
        this.helperFiles = listFilesAsMap(helperDir);

        this.language = new PlainCppGradingLanguage();
    }

    @BeforeMethod
    public void setUp() {
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

    protected final BlackBoxGradingResult runEngine(BlackBoxGradingEngine grader, BlackBoxGradingConfig config) throws GradingException {
        SandboxFactory sandboxFactory = new FakeSandboxFactory(sandboxDir);
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
