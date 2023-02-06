package judgels.sandalphon.problem.programming;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import judgels.fs.FileInfo;
import judgels.fs.FileSystem;
import judgels.gabriel.api.GradingConfig;
import judgels.gabriel.api.LanguageRestriction;
import judgels.gabriel.engines.GradingEngineRegistry;
import judgels.sandalphon.problem.base.AbstractProblemStore;
import judgels.sandalphon.problem.base.ProblemFs;

public final class ProgrammingProblemStore extends AbstractProblemStore {
    private final ObjectMapper mapper;
    private final FileSystem problemFs;

    @Inject
    public ProgrammingProblemStore(ObjectMapper mapper, @ProblemFs FileSystem problemFs) {
        super(mapper, problemFs);
        this.mapper = mapper;
        this.problemFs = problemFs;
    }

    public void initProgrammingProblem(String problemJid, String gradingEngine) {
        problemFs.createDirectory(getGradingDirPath(null, problemJid));

        problemFs.createDirectory(getGradingTestDataDirPath(null, problemJid));
        problemFs.createFile(getGradingTestDataDirPath(null, problemJid).resolve(".gitkeep"));

        problemFs.createDirectory(getGradingHelpersDirPath(null, problemJid));
        problemFs.createFile(getGradingHelpersDirPath(null, problemJid).resolve(".gitkeep"));

        problemFs.writeToFile(getGradingEngineFilePath(null, problemJid), gradingEngine);
        problemFs.writeToFile(getLanguageRestrictionFilePath(null, problemJid), writeObj(LanguageRestriction.noRestriction()));

        GradingConfig config = GradingEngineRegistry.getInstance().get(gradingEngine).createDefaultConfig();
        problemFs.writeToFile(getGradingConfigFilePath(null, problemJid), writeObj(config));

        updateGradingLastUpdateTime(null, problemJid);
    }

    public Date getGradingLastUpdateTime(String userJid, String problemJid) {
        String lastUpdateTime = problemFs.readFromFile(getGradingLastUpdateTimeFilePath(userJid, problemJid));
        return new Date(Long.parseLong(lastUpdateTime));
    }

    public GradingConfig getGradingConfig(String userJid, String problemJid) {
        String gradingEngine = problemFs.readFromFile(getGradingEngineFilePath(userJid, problemJid));
        String gradingConfig = problemFs.readFromFile(getGradingConfigFilePath(userJid, problemJid));

        try {
            return GradingEngineRegistry.getInstance().get(gradingEngine).parseConfig(mapper, gradingConfig);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateGradingConfig(String userJid, String problemJid, GradingConfig gradingConfig) {
        problemFs.writeToFile(getGradingConfigFilePath(userJid, problemJid), writeObj(gradingConfig));

        updateGradingLastUpdateTime(userJid, problemJid);
    }

    public String getGradingEngine(String userJid, String problemJid) {
        return problemFs.readFromFile(getGradingEngineFilePath(userJid, problemJid));
    }

    public void updateGradingEngine(String userJid, String problemJid, String gradingEngine) {
        problemFs.writeToFile(getGradingEngineFilePath(userJid, problemJid), gradingEngine);

        updateGradingLastUpdateTime(userJid, problemJid);
    }

    public LanguageRestriction getLanguageRestriction(String userJid, String problemJid) {
        String languageRestriction = problemFs.readFromFile(getLanguageRestrictionFilePath(userJid, problemJid));
        try {
            return mapper.readValue(languageRestriction, LanguageRestriction.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateLanguageRestriction(String userJid, String problemJid, LanguageRestriction languageRestriction) {
        problemFs.writeToFile(getLanguageRestrictionFilePath(userJid, problemJid), writeObj(languageRestriction));

        updateGradingLastUpdateTime(userJid, problemJid);
    }

    public void uploadGradingTestDataFile(String userJid, String problemJid, File testDataFile, String filename) {
        try {
            problemFs.uploadPublicFile(getGradingTestDataDirPath(userJid, problemJid).resolve(filename), new FileInputStream(testDataFile));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        updateGradingLastUpdateTime(userJid, problemJid);
    }

    public void uploadGradingTestDataFileZipped(String userJid, String problemJid, File testDataFileZipped) {
        problemFs.uploadZippedFiles(getGradingTestDataDirPath(userJid, problemJid), testDataFileZipped, false);

        updateGradingLastUpdateTime(userJid, problemJid);
    }

    public void uploadGradingHelperFile(String userJid, String problemJid, File helperFile, String filename) {
        try {
            problemFs.uploadPublicFile(getGradingHelpersDirPath(userJid, problemJid).resolve(filename), new FileInputStream(helperFile));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        updateGradingLastUpdateTime(userJid, problemJid);
    }

    public void uploadGradingHelperFileZipped(String userJid, String problemJid, File helperFileZipped) {
        problemFs.uploadZippedFiles(getGradingHelpersDirPath(userJid, problemJid), helperFileZipped, false);

        updateGradingLastUpdateTime(userJid, problemJid);
    }

    public List<FileInfo> getGradingTestDataFiles(String userJid, String problemJid) {
        return problemFs.listFilesInDirectory(getGradingTestDataDirPath(userJid, problemJid));
    }

    public List<FileInfo> getGradingHelperFiles(String userJid, String problemJid) {
        return problemFs.listFilesInDirectory(getGradingHelpersDirPath(userJid, problemJid));
    }

    public String getGradingTestDataFileURL(String userJid, String problemJid, String filename) {
        return problemFs.getPublicFileUrl(getGradingTestDataDirPath(userJid, problemJid).resolve(filename));
    }


    public String getGradingHelperFileURL(String userJid, String problemJid, String filename) {
        return problemFs.getPublicFileUrl(getGradingHelpersDirPath(userJid, problemJid).resolve(filename));
    }

    public ByteArrayOutputStream getZippedGradingFilesStream(String problemJid) throws IOException {
        throw new UnsupportedEncodingException();
    }

    private void updateGradingLastUpdateTime(String userJid, String problemJid) {
        problemFs.writeToFile(getGradingLastUpdateTimeFilePath(userJid, problemJid), "" + System.currentTimeMillis());
    }

    private Path getGradingDirPath(String userJid, String problemJid) {
        return getRootDirPath(userJid, problemJid).resolve("grading");
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
