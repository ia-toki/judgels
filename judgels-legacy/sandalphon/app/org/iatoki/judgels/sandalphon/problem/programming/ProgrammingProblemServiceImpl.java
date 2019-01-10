package org.iatoki.judgels.sandalphon.problem.programming;

import com.google.gson.Gson;
import org.iatoki.judgels.FileInfo;
import org.iatoki.judgels.FileSystemProvider;
import org.iatoki.judgels.gabriel.GradingConfig;
import org.iatoki.judgels.gabriel.GradingEngineRegistry;
import org.iatoki.judgels.sandalphon.problem.programming.grading.LanguageRestriction;
import org.iatoki.judgels.sandalphon.problem.base.ProblemFileSystemProvider;
import org.iatoki.judgels.sandalphon.problem.base.ProblemServiceImplUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@Singleton
public final class ProgrammingProblemServiceImpl implements ProgrammingProblemService {

    private final FileSystemProvider problemFileSystemProvider;

    @Inject
    public ProgrammingProblemServiceImpl(@ProblemFileSystemProvider FileSystemProvider problemFileSystemProvider) {
        this.problemFileSystemProvider = problemFileSystemProvider;
    }

    public void initProgrammingProblem(String problemJid, String gradingEngine) throws IOException {
        problemFileSystemProvider.createDirectory(getGradingDirPath(null, problemJid));

        problemFileSystemProvider.createDirectory(getGradingTestDataDirPath(null, problemJid));
        problemFileSystemProvider.createFile(ProblemServiceImplUtils.appendPath(getGradingTestDataDirPath(null, problemJid), ".gitkeep"));

        problemFileSystemProvider.createDirectory(getGradingHelpersDirPath(null, problemJid));
        problemFileSystemProvider.createFile(ProblemServiceImplUtils.appendPath(getGradingHelpersDirPath(null, problemJid), ".gitkeep"));

        problemFileSystemProvider.writeToFile(getGradingEngineFilePath(null, problemJid), gradingEngine);
        problemFileSystemProvider.writeToFile(getLanguageRestrictionFilePath(null, problemJid), new Gson().toJson(LanguageRestriction.defaultRestriction()));

        GradingConfig config = GradingEngineRegistry.getInstance().getEngine(gradingEngine).createDefaultGradingConfig();
        problemFileSystemProvider.writeToFile(getGradingConfigFilePath(null, problemJid), new Gson().toJson(config));

        updateGradingLastUpdateTime(null, problemJid);
    }

    @Override
    public Date getGradingLastUpdateTime(String userJid, String problemJid) throws IOException {
        String lastUpdateTime = problemFileSystemProvider.readFromFile(getGradingLastUpdateTimeFilePath(userJid, problemJid));
        return new Date(Long.parseLong(lastUpdateTime));
    }

    @Override
    public GradingConfig getGradingConfig(String userJid, String problemJid) throws IOException {
        String gradingEngine = problemFileSystemProvider.readFromFile(getGradingEngineFilePath(userJid, problemJid));
        String gradingConfig = problemFileSystemProvider.readFromFile(getGradingConfigFilePath(userJid, problemJid));

        return GradingEngineRegistry.getInstance().getEngine(gradingEngine).createGradingConfigFromJson(gradingConfig);
    }

    @Override
    public void updateGradingConfig(String userJid, String problemJid, GradingConfig gradingConfig) throws IOException {
        problemFileSystemProvider.writeToFile(getGradingConfigFilePath(userJid, problemJid), new Gson().toJson(gradingConfig));

        updateGradingLastUpdateTime(userJid, problemJid);
    }

    @Override
    public String getGradingEngine(String userJid, String problemJid) throws IOException {
        return problemFileSystemProvider.readFromFile(getGradingEngineFilePath(userJid, problemJid));
    }

    @Override
    public void updateGradingEngine(String userJid, String problemJid, String gradingEngine) throws IOException {
        problemFileSystemProvider.writeToFile(getGradingEngineFilePath(userJid, problemJid), gradingEngine);

        updateGradingLastUpdateTime(userJid, problemJid);
    }

    @Override
    public LanguageRestriction getLanguageRestriction(String userJid, String problemJid) throws IOException {
        String languageRestriction = problemFileSystemProvider.readFromFile(getLanguageRestrictionFilePath(userJid, problemJid));
        return new Gson().fromJson(languageRestriction, LanguageRestriction.class);
    }

    @Override
    public void updateLanguageRestriction(String userJid, String problemJid, LanguageRestriction languageRestriction) throws IOException {
        problemFileSystemProvider.writeToFile(getLanguageRestrictionFilePath(userJid, problemJid), new Gson().toJson(languageRestriction));

        updateGradingLastUpdateTime(userJid, problemJid);
    }

    @Override
    public void uploadGradingTestDataFile(String userJid, String problemJid, File testDataFile, String filename) throws IOException {
        problemFileSystemProvider.uploadFile(getGradingTestDataDirPath(userJid, problemJid), testDataFile, filename);

        updateGradingLastUpdateTime(userJid, problemJid);
    }

    @Override
    public void uploadGradingTestDataFileZipped(String userJid, String problemJid, File testDataFileZipped) throws IOException {
        problemFileSystemProvider.uploadZippedFiles(getGradingTestDataDirPath(userJid, problemJid), testDataFileZipped, false);

        updateGradingLastUpdateTime(userJid, problemJid);
    }

    @Override
    public void uploadGradingHelperFile(String userJid, String problemJid, File helperFile, String filename) throws IOException {
        problemFileSystemProvider.uploadFile(getGradingHelpersDirPath(userJid, problemJid), helperFile, filename);

        updateGradingLastUpdateTime(userJid, problemJid);
    }

    @Override
    public void uploadGradingHelperFileZipped(String userJid, String problemJid, File helperFileZipped) throws IOException {
        problemFileSystemProvider.uploadZippedFiles(getGradingHelpersDirPath(userJid, problemJid), helperFileZipped, false);

        updateGradingLastUpdateTime(userJid, problemJid);
    }

    @Override
    public List<FileInfo> getGradingTestDataFiles(String userJid, String problemJid) {
        return problemFileSystemProvider.listFilesInDirectory(getGradingTestDataDirPath(userJid, problemJid));
    }

    @Override
    public List<FileInfo> getGradingHelperFiles(String userJid, String problemJid) {
        return problemFileSystemProvider.listFilesInDirectory(getGradingHelpersDirPath(userJid, problemJid));
    }

    @Override
    public String getGradingTestDataFileURL(String userJid, String problemJid, String filename) {
        return problemFileSystemProvider.getURL(ProblemServiceImplUtils.appendPath(getGradingTestDataDirPath(userJid, problemJid), filename));
    }


    @Override
    public String getGradingHelperFileURL(String userJid, String problemJid, String filename) {
        return problemFileSystemProvider.getURL(ProblemServiceImplUtils.appendPath(getGradingHelpersDirPath(userJid, problemJid), filename));
    }

    @Override
    public ByteArrayOutputStream getZippedGradingFilesStream(String problemJid) throws IOException {
        return problemFileSystemProvider.getZippedFilesInDirectory(getGradingDirPath(null, problemJid));
    }

    private void updateGradingLastUpdateTime(String userJid, String problemJid) throws IOException {
        problemFileSystemProvider.writeToFile(getGradingLastUpdateTimeFilePath(userJid, problemJid), "" + System.currentTimeMillis());
    }

    private List<String> getGradingDirPath(String userJid, String problemJid) {
        return ProblemServiceImplUtils.appendPath(ProblemServiceImplUtils.getRootDirPath(problemFileSystemProvider, userJid, problemJid), "grading");
    }

    private List<String> getGradingTestDataDirPath(String userJid, String problemJid) {
        return ProblemServiceImplUtils.appendPath(getGradingDirPath(userJid, problemJid), "testdata");
    }

    private List<String> getGradingHelpersDirPath(String userJid, String problemJid) {
        return ProblemServiceImplUtils.appendPath(getGradingDirPath(userJid, problemJid), "helpers");
    }

    private List<String> getGradingConfigFilePath(String userJid, String problemJid) {
        return ProblemServiceImplUtils.appendPath(getGradingDirPath(userJid, problemJid), "config.json");
    }

    private List<String> getGradingEngineFilePath(String userJid, String problemJid) {
        return ProblemServiceImplUtils.appendPath(getGradingDirPath(userJid, problemJid), "engine.txt");
    }

    private List<String> getLanguageRestrictionFilePath(String userJid, String problemJid) {
        return ProblemServiceImplUtils.appendPath(getGradingDirPath(userJid, problemJid), "languageRestriction.txt");
    }

    private List<String> getGradingLastUpdateTimeFilePath(String userJid, String problemJid) {
        return ProblemServiceImplUtils.appendPath(getGradingDirPath(userJid, problemJid), "lastUpdateTime.txt");
    }
}
