package judgels.sandalphon.problem.base.editorial;

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
import judgels.sandalphon.api.problem.ProblemEditorial;
import judgels.sandalphon.problem.base.BaseProblemStore;
import judgels.sandalphon.problem.base.ProblemFs;
import judgels.sandalphon.resource.StatementLanguageStatus;

public class ProblemEditorialStore extends BaseProblemStore {
    @Inject
    public ProblemEditorialStore(ObjectMapper mapper, @ProblemFs FileSystem problemFs) {
        super(mapper, problemFs);
    }

    public void initEditorials(String userJid, String problemJid, String initialLanguageCode) {
        Path editorialsDirPath = getEditorialsDirPath(userJid, problemJid);
        problemFs.createDirectory(editorialsDirPath);

        Path editorialDirPath = getEditorialDirPath(userJid, problemJid, initialLanguageCode);
        problemFs.createDirectory(editorialDirPath);

        Path mediaDirPath = getEditorialMediaDirPath(userJid, problemJid);
        problemFs.createDirectory(mediaDirPath);
        problemFs.createFile(mediaDirPath.resolve(".gitkeep"));

        problemFs.createFile(getEditorialTextFilePath(userJid, problemJid, initialLanguageCode));
        problemFs.writeToFile(getEditorialDefaultLanguageFilePath(userJid, problemJid), initialLanguageCode);

        Map<String, StatementLanguageStatus> initialLanguage = ImmutableMap.of(initialLanguageCode, StatementLanguageStatus.ENABLED);
        problemFs.writeToFile(getEditorialAvailableLanguagesFilePath(userJid, problemJid), writeObj(initialLanguage));
    }

    public Map<String, StatementLanguageStatus> getEditorialAvailableLanguages(String userJid, String problemJid) {
        String languages = problemFs.readFromFile(getEditorialAvailableLanguagesFilePath(userJid, problemJid));
        try {
            return mapper.readValue(languages, new TypeReference<Map<String, StatementLanguageStatus>>() {});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Set<String> getEditorialLanguages(String userJid, String problemJid) {
        return getEditorialAvailableLanguages(userJid, problemJid).entrySet().stream()
                .filter(e -> e.getValue() == StatementLanguageStatus.ENABLED)
                .map(e -> e.getKey())
                .collect(Collectors.toSet());
    }

    public Set<String> getEditorialEnabledLanguages(String userJid, String problemJid) {
        return getEditorialAvailableLanguages(userJid, problemJid)
                .entrySet()
                .stream()
                .filter(e -> e.getValue() == StatementLanguageStatus.ENABLED)
                .map(e -> e.getKey())
                .collect(Collectors.toSet());
    }

    public void addEditorialLanguage(String userJid, String problemJid, String language) {
        Map<String, StatementLanguageStatus> availableLanguages = getEditorialAvailableLanguages(userJid, problemJid);
        availableLanguages.put(language, StatementLanguageStatus.ENABLED);

        problemFs.writeToFile(getEditorialTextFilePath(userJid, problemJid, language), "");
        problemFs.writeToFile(getEditorialAvailableLanguagesFilePath(userJid, problemJid), writeObj(availableLanguages));
    }

    public void enableEditorialLanguage(String userJid, String problemJid, String language) {
        Map<String, StatementLanguageStatus> availableLanguages = getEditorialAvailableLanguages(userJid, problemJid);
        availableLanguages.put(language, StatementLanguageStatus.ENABLED);
        problemFs.writeToFile(getEditorialAvailableLanguagesFilePath(userJid, problemJid), writeObj(availableLanguages));
    }

    public void disableEditorialLanguage(String userJid, String problemJid, String language) {
        Map<String, StatementLanguageStatus> availableLanguages = getEditorialAvailableLanguages(userJid, problemJid);
        availableLanguages.put(language, StatementLanguageStatus.DISABLED);
        problemFs.writeToFile(getEditorialAvailableLanguagesFilePath(userJid, problemJid), writeObj(availableLanguages));
    }

    public void makeEditorialDefaultLanguage(String userJid, String problemJid, String language) {
        problemFs.writeToFile(getEditorialDefaultLanguageFilePath(userJid, problemJid), language);
    }

    public String getEditorialDefaultLanguage(String userJid, String problemJid) {
        return problemFs.readFromFile(getEditorialDefaultLanguageFilePath(userJid, problemJid));
    }

    public boolean hasEditorial(String userJid, String problemJid) {
        return problemFs.directoryExists(getEditorialsDirPath(userJid, problemJid));
    }

    public ProblemEditorial getEditorial(String userJid, String problemJid, String language) {
        String text = problemFs.readFromFile(getEditorialTextFilePath(userJid, problemJid, language));

        return new ProblemEditorial.Builder().text(text).build();
    }

    public void updateEditorial(String userJid, String problemJid, String language, ProblemEditorial editorial) {
        problemFs.writeToFile(getEditorialTextFilePath(userJid, problemJid, language), editorial.getText());
    }

    public void uploadEditorialMediaFile(String userJid, String problemJid, InputStream mediaFile, String filename) {
        Path mediaDirPath = getEditorialMediaDirPath(userJid, problemJid);
        problemFs.uploadPublicFile(mediaDirPath.resolve(filename), mediaFile);
    }

    public void uploadEditorialMediaFileZipped(String userJid, String problemJid, InputStream mediaFileZipped) {
        Path mediaDirPath = getEditorialMediaDirPath(userJid, problemJid);
        problemFs.uploadZippedFiles(mediaDirPath, mediaFileZipped);
    }

    public List<FileInfo> getEditorialMediaFiles(String userJid, String problemJid) {
        Path mediaDirPath = getEditorialMediaDirPath(userJid, problemJid);
        return problemFs.listFilesInDirectory(mediaDirPath);
    }

    public String getEditorialMediaFileURL(String userJid, String problemJid, String filename) {
        Path mediaFilePath = getEditorialMediaDirPath(userJid, problemJid).resolve(filename);
        return problemFs.getPublicFileUrl(mediaFilePath);
    }

    private Path getEditorialsDirPath(String userJid, String problemJid) {
        return getRootDirPath(userJid, problemJid).resolve("editorials");
    }

    private Path getEditorialDirPath(String userJid, String problemJid, String language) {
        return getEditorialsDirPath(userJid, problemJid).resolve(language);
    }

    private Path getEditorialTextFilePath(String userJid, String problemJid, String language) {
        return getEditorialDirPath(userJid, problemJid, language).resolve("text.html");
    }

    private Path getEditorialDefaultLanguageFilePath(String userJid, String problemJid) {
        return getEditorialsDirPath(userJid, problemJid).resolve("defaultLanguage.txt");
    }

    private Path getEditorialAvailableLanguagesFilePath(String userJid, String problemJid) {
        return getEditorialsDirPath(userJid, problemJid).resolve("availableLanguages.txt");
    }

    private Path getEditorialMediaDirPath(String userJid, String problemJid) {
        return getEditorialsDirPath(userJid, problemJid).resolve("resources");
    }
}
