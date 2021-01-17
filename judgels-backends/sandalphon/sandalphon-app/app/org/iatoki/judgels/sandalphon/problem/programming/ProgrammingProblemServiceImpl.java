package org.iatoki.judgels.sandalphon.problem.programming;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.fs.FileInfo;
import judgels.fs.FileSystem;
import judgels.gabriel.api.GradingConfig;
import judgels.gabriel.api.LanguageRestriction;
import judgels.gabriel.engines.GradingEngineRegistry;
import org.iatoki.judgels.sandalphon.problem.base.ProblemFileSystemProvider;
import org.iatoki.judgels.sandalphon.problem.base.ProblemServiceImplUtils;

@Singleton
public final class ProgrammingProblemServiceImpl implements ProgrammingProblemService {
    private static final ObjectMapper MAPPER = new ObjectMapper().registerModules(new Jdk8Module(), new GuavaModule());

    private final FileSystem problemFs;

    @Inject
    public ProgrammingProblemServiceImpl(@ProblemFileSystemProvider FileSystem problemFs) {
        this.problemFs = problemFs;
    }

    public void initProgrammingProblem(String problemJid, String gradingEngine) throws IOException {
        problemFs.createDirectory(getGradingDirPath(null, problemJid));

        problemFs.createDirectory(getGradingTestDataDirPath(null, problemJid));
        problemFs.createFile(getGradingTestDataDirPath(null, problemJid).resolve(".gitkeep"));

        problemFs.createDirectory(getGradingHelpersDirPath(null, problemJid));
        problemFs.createFile(getGradingHelpersDirPath(null, problemJid).resolve(".gitkeep"));

        problemFs.writeToFile(getGradingEngineFilePath(null, problemJid), gradingEngine);
        problemFs.writeToFile(getLanguageRestrictionFilePath(null, problemJid), MAPPER.writeValueAsString(LanguageRestriction.noRestriction()));

        GradingConfig config = GradingEngineRegistry.getInstance().get(gradingEngine).createDefaultConfig();
        problemFs.writeToFile(getGradingConfigFilePath(null, problemJid), MAPPER.writeValueAsString(config));

        updateGradingLastUpdateTime(null, problemJid);
    }

    @Override
    public Date getGradingLastUpdateTime(String userJid, String problemJid) throws IOException {
        String lastUpdateTime = problemFs.readFromFile(getGradingLastUpdateTimeFilePath(userJid, problemJid));
        return new Date(Long.parseLong(lastUpdateTime));
    }

    @Override
    public GradingConfig getGradingConfig(String userJid, String problemJid) throws IOException {
        String gradingEngine = problemFs.readFromFile(getGradingEngineFilePath(userJid, problemJid));
        String gradingConfig = problemFs.readFromFile(getGradingConfigFilePath(userJid, problemJid));

        return GradingEngineRegistry.getInstance().get(gradingEngine).parseConfig(MAPPER, gradingConfig);
    }

    @Override
    public void updateGradingConfig(String userJid, String problemJid, GradingConfig gradingConfig) throws IOException {
        problemFs.writeToFile(getGradingConfigFilePath(userJid, problemJid), MAPPER.writeValueAsString(gradingConfig));

        updateGradingLastUpdateTime(userJid, problemJid);
    }

    @Override
    public String getGradingEngine(String userJid, String problemJid) throws IOException {
        return problemFs.readFromFile(getGradingEngineFilePath(userJid, problemJid));
    }

    @Override
    public void updateGradingEngine(String userJid, String problemJid, String gradingEngine) throws IOException {
        problemFs.writeToFile(getGradingEngineFilePath(userJid, problemJid), gradingEngine);

        updateGradingLastUpdateTime(userJid, problemJid);
    }

    @Override
    public LanguageRestriction getLanguageRestriction(String userJid, String problemJid) throws IOException {
        String languageRestriction = problemFs.readFromFile(getLanguageRestrictionFilePath(userJid, problemJid));
        return MAPPER.readValue(languageRestriction, LanguageRestriction.class);
    }

    @Override
    public void updateLanguageRestriction(String userJid, String problemJid, LanguageRestriction languageRestriction) throws IOException {
        problemFs.writeToFile(getLanguageRestrictionFilePath(userJid, problemJid), MAPPER.writeValueAsString(languageRestriction));

        updateGradingLastUpdateTime(userJid, problemJid);
    }

    @Override
    public void uploadGradingTestDataFile(String userJid, String problemJid, File testDataFile, String filename) throws IOException {
        problemFs.uploadPublicFile(getGradingTestDataDirPath(userJid, problemJid).resolve(filename), new FileInputStream(testDataFile));

        updateGradingLastUpdateTime(userJid, problemJid);
    }

    @Override
    public void uploadGradingTestDataFileZipped(String userJid, String problemJid, File testDataFileZipped) throws IOException {
        problemFs.uploadZippedFiles(getGradingTestDataDirPath(userJid, problemJid), testDataFileZipped, false);

        updateGradingLastUpdateTime(userJid, problemJid);
    }

    @Override
    public void uploadGradingHelperFile(String userJid, String problemJid, File helperFile, String filename) throws IOException {
        problemFs.uploadPublicFile(getGradingHelpersDirPath(userJid, problemJid).resolve(filename), new FileInputStream(helperFile));

        updateGradingLastUpdateTime(userJid, problemJid);
    }

    @Override
    public void uploadGradingHelperFileZipped(String userJid, String problemJid, File helperFileZipped) throws IOException {
        problemFs.uploadZippedFiles(getGradingHelpersDirPath(userJid, problemJid), helperFileZipped, false);

        updateGradingLastUpdateTime(userJid, problemJid);
    }

    @Override
    public List<FileInfo> getGradingTestDataFiles(String userJid, String problemJid) {
        return problemFs.listFilesInDirectory(getGradingTestDataDirPath(userJid, problemJid));
    }

    @Override
    public List<FileInfo> getGradingHelperFiles(String userJid, String problemJid) {
        return problemFs.listFilesInDirectory(getGradingHelpersDirPath(userJid, problemJid));
    }

    @Override
    public String getGradingTestDataFileURL(String userJid, String problemJid, String filename) {
        return problemFs.getPublicFileUrl(getGradingTestDataDirPath(userJid, problemJid).resolve(filename));
    }


    @Override
    public String getGradingHelperFileURL(String userJid, String problemJid, String filename) {
        return problemFs.getPublicFileUrl(getGradingHelpersDirPath(userJid, problemJid).resolve(filename));
    }

    @Override
    public ByteArrayOutputStream getZippedGradingFilesStream(String problemJid) throws IOException {
        throw new UnsupportedEncodingException();
    }

    private void updateGradingLastUpdateTime(String userJid, String problemJid) throws IOException {
        problemFs.writeToFile(getGradingLastUpdateTimeFilePath(userJid, problemJid), "" + System.currentTimeMillis());
    }

    private Path getGradingDirPath(String userJid, String problemJid) {
        return ProblemServiceImplUtils.getRootDirPath(problemFs, userJid, problemJid).resolve("grading");
    }

    private Path getGradingTestDataDirPath(String userJid, String problemJid) {
        return getGradingDirPath(userJid, problemJid).resolve("testdata");
    }

    private Path getGradingHelpersDirPath(String userJid, String problemJid) {
        return getGradingDirPath(userJid, problemJid).resolve("helpers");
    }

    private Path getGradingConfigFilePath(String userJid, String problemJid) {
        return getGradingDirPath(userJid, problemJid).resolve("config.json");
    }

    private Path getGradingEngineFilePath(String userJid, String problemJid) {
        return getGradingDirPath(userJid, problemJid).resolve("engine.txt");
    }

    private Path getLanguageRestrictionFilePath(String userJid, String problemJid) {
        return getGradingDirPath(userJid, problemJid).resolve("languageRestriction.txt");
    }

    private Path getGradingLastUpdateTimeFilePath(String userJid, String problemJid) {
        return getGradingDirPath(userJid, problemJid).resolve("lastUpdateTime.txt");
    }
}
