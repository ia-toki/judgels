package judgels.sandalphon.lesson.statement;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.fs.FileInfo;
import judgels.fs.FileSystem;
import judgels.sandalphon.api.lesson.LessonStatement;
import judgels.sandalphon.lesson.BaseLessonStore;
import judgels.sandalphon.lesson.LessonFs;
import judgels.sandalphon.persistence.LessonDao;
import judgels.sandalphon.problem.base.statement.ProblemStatementUtils;
import judgels.sandalphon.resource.StatementLanguageStatus;

public class LessonStatementStore extends BaseLessonStore {
    private final LessonDao lessonDao;

    @Inject
    public LessonStatementStore(ObjectMapper mapper, @LessonFs FileSystem lessonFs, LessonDao lessonDao) {
        super(mapper, lessonFs);
        this.lessonDao = lessonDao;
    }

    public void initStatements(String lessonJid, String language) {
        Path statementsDirPath = getStatementsDirPath(null, lessonJid);
        lessonFs.createDirectory(statementsDirPath);

        Path statementDirPath = getStatementDirPath(null, lessonJid, language);
        lessonFs.createDirectory(statementDirPath);

        Path mediaDirPath = getStatementMediaDirPath(null, lessonJid);
        lessonFs.createDirectory(mediaDirPath);
        lessonFs.createFile(mediaDirPath.resolve(".gitkeep"));

        lessonFs.createFile(getStatementTitleFilePath(null, lessonJid, language));
        lessonFs.createFile(getStatementTextFilePath(null, lessonJid, language));
        lessonFs.writeToFile(getStatementDefaultLanguageFilePath(null, lessonJid), language);

        Map<String, StatementLanguageStatus>
                initialLanguage = ImmutableMap.of(language, StatementLanguageStatus.ENABLED);
        lessonFs.writeToFile(getStatementAvailableLanguagesFilePath(null, lessonJid), writeObj(initialLanguage));

        updateStatement(null, lessonJid, language, new LessonStatement.Builder()
                .title(ProblemStatementUtils.getDefaultTitle(language))
                .text(LessonStatementUtils.getDefaultText(language))
                .build());
    }

    public LessonStatement getStatement(String userJid, String lessonJid, String languageCode) {
        String title = lessonFs.readFromFile(getStatementTitleFilePath(userJid, lessonJid, languageCode));
        String text = lessonFs.readFromFile(getStatementTextFilePath(userJid, lessonJid, languageCode));

        return new LessonStatement.Builder().title(title).text(text).build();
    }

    public void updateStatement(String userJid, String lessonJid, String languageCode, LessonStatement statement) {
        lessonFs.writeToFile(getStatementTitleFilePath(userJid, lessonJid, languageCode), statement.getTitle());
        lessonFs.writeToFile(getStatementTextFilePath(userJid, lessonJid, languageCode), statement.getText());
    }

    public Map<String, StatementLanguageStatus> getAvailableLanguages(String userJid, String lessonJid) {
        String languages = lessonFs.readFromFile(getStatementAvailableLanguagesFilePath(userJid, lessonJid));
        try {
            return mapper.readValue(languages, new TypeReference<Map<String, StatementLanguageStatus>>() {});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Set<String> getEnabledLanguages(String userJid, String lessonJid) {
        return getAvailableLanguages(userJid, lessonJid)
                .entrySet()
                .stream()
                .filter(e -> e.getValue() == StatementLanguageStatus.ENABLED)
                .map(e -> e.getKey())
                .collect(Collectors.toSet());
    }

    public void addLanguage(String userJid, String lessonJid, String language) {
        Map<String, StatementLanguageStatus> availableLanguages = getAvailableLanguages(userJid, lessonJid);
        availableLanguages.put(language, StatementLanguageStatus.ENABLED);

        LessonStatement statement = getStatement(userJid, lessonJid, getDefaultLanguage(userJid, lessonJid));
        lessonFs.writeToFile(getStatementTitleFilePath(userJid, lessonJid, language), statement.getTitle());
        lessonFs.writeToFile(getStatementTextFilePath(userJid, lessonJid, language), statement.getText());
        lessonFs.writeToFile(getStatementAvailableLanguagesFilePath(userJid, lessonJid), writeObj(availableLanguages));
    }

    public void enableLanguage(String userJid, String lessonJid, String language) {
        Map<String, StatementLanguageStatus> availableLanguages = getAvailableLanguages(userJid, lessonJid);
        availableLanguages.put(language, StatementLanguageStatus.ENABLED);
        lessonFs.writeToFile(getStatementAvailableLanguagesFilePath(userJid, lessonJid), writeObj(availableLanguages));
    }

    public void disableLanguage(String userJid, String lessonJid, String language) {
        Map<String, StatementLanguageStatus> availableLanguages = getAvailableLanguages(userJid, lessonJid);
        availableLanguages.put(language, StatementLanguageStatus.DISABLED);
        lessonFs.writeToFile(getStatementAvailableLanguagesFilePath(userJid, lessonJid), writeObj(availableLanguages));
    }

    public void makeDefaultLanguage(String userJid, String lessonJid, String language) {
        lessonFs.writeToFile(getStatementDefaultLanguageFilePath(userJid, lessonJid), language);
    }

    public String getDefaultLanguage(String userJid, String lessonJid) {
        return lessonFs.readFromFile(getStatementDefaultLanguageFilePath(userJid, lessonJid));
    }

    public Map<String, String> getTitlesByLanguage(String userJid, String lessonJid) {
        Map<String, StatementLanguageStatus> availableLanguages = getAvailableLanguages(userJid, lessonJid);

        ImmutableMap.Builder<String, String> titlesByLanguageBuilder = ImmutableMap.builder();

        for (Map.Entry<String, StatementLanguageStatus> entry : availableLanguages.entrySet()) {
            if (entry.getValue() == StatementLanguageStatus.ENABLED) {
                String title = lessonFs.readFromFile(getStatementTitleFilePath(userJid, lessonJid, entry.getKey()));
                titlesByLanguageBuilder.put(entry.getKey(), title);
            }
        }

        return titlesByLanguageBuilder.build();
    }

    public void uploadStatementMediaFile(String userJid, String lessonJid, InputStream mediaFile, String filename) {
        Path mediaDirPath = getStatementMediaDirPath(userJid, lessonJid);
        lessonFs.uploadPublicFile(mediaDirPath.resolve(filename), mediaFile);
    }

    public void uploadStatementMediaFileZipped(String userJid, String lessonJid, InputStream mediaFileZipped) {
        Path mediaDirPath = getStatementMediaDirPath(userJid, lessonJid);
        lessonFs.uploadZippedFiles(mediaDirPath, mediaFileZipped);
    }

    public List<FileInfo> getStatementMediaFiles(String userJid, String lessonJid) {
        Path mediaDirPath = getStatementMediaDirPath(userJid, lessonJid);
        return lessonFs.listFilesInDirectory(mediaDirPath);
    }

    public String getStatementMediaFileURL(String userJid, String lessonJid, String filename) {
        Path mediaFilePath = getStatementMediaDirPath(userJid, lessonJid).resolve(filename);
        return lessonFs.getPublicFileUrl(mediaFilePath);
    }

    private Path getStatementsDirPath(String userJid, String lessonJid) {
        return getRootDirPath(lessonFs, userJid, lessonJid).resolve("statements");
    }

    private Path getStatementDirPath(String userJid, String lessonJid, String languageCode) {
        return getStatementsDirPath(userJid, lessonJid).resolve(languageCode);
    }

    private Path getStatementTitleFilePath(String userJid, String lessonJid, String languageCode) {
        return getStatementDirPath(userJid, lessonJid, languageCode).resolve("title.txt");
    }

    private Path getStatementTextFilePath(String userJid, String lessonJid, String languageCode) {
        return getStatementDirPath(userJid, lessonJid, languageCode).resolve("text.html");
    }

    private Path getStatementDefaultLanguageFilePath(String userJid, String lessonJid) {
        return getStatementsDirPath(userJid, lessonJid).resolve("defaultLanguage.txt");
    }

    private Path getStatementAvailableLanguagesFilePath(String userJid, String lessonJid) {
        return getStatementsDirPath(userJid, lessonJid).resolve("availableLanguages.txt");
    }

    private Path getStatementMediaDirPath(String userJid, String lessonJid) {
        return getStatementsDirPath(userJid, lessonJid).resolve("resources");
    }
}
