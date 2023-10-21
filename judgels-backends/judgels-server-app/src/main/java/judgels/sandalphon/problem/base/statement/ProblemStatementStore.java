package judgels.sandalphon.problem.base.statement;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.fs.FileInfo;
import judgels.fs.FileSystem;
import judgels.gabriel.languages.GradingLanguageRegistry;
import judgels.sandalphon.api.problem.ProblemStatement;
import judgels.sandalphon.api.problem.ProblemType;
import judgels.sandalphon.api.problem.programming.ProblemSkeleton;
import judgels.sandalphon.problem.base.BaseProblemStore;
import judgels.sandalphon.problem.base.ProblemFs;
import judgels.sandalphon.problem.bundle.statement.BundleProblemStatementUtils;
import judgels.sandalphon.problem.programming.statement.ProgrammingProblemStatementUtils;
import judgels.sandalphon.resource.StatementLanguageStatus;
import org.apache.commons.io.FilenameUtils;

public class ProblemStatementStore extends BaseProblemStore {
    @Inject
    public ProblemStatementStore(ObjectMapper mapper, @ProblemFs FileSystem problemFs) {
        super(mapper, problemFs);
    }

    public void initStatements(String problemJid, ProblemType type, String language) {
        Path statementsDirPath = getStatementsDirPath(null, problemJid);
        problemFs.createDirectory(statementsDirPath);

        Path statementDirPath = getStatementDirPath(null, problemJid, language);
        problemFs.createDirectory(statementDirPath);

        Path mediaDirPath = getStatementMediaDirPath(null, problemJid);
        problemFs.createDirectory(mediaDirPath);
        problemFs.createFile(mediaDirPath.resolve(".gitkeep"));

        problemFs.createFile(getStatementTitleFilePath(null, problemJid, language));
        problemFs.createFile(getStatementTextFilePath(null, problemJid, language));
        problemFs.writeToFile(getStatementDefaultLanguageFilePath(null, problemJid), language);

        Map<String, StatementLanguageStatus> initialLanguage = ImmutableMap.of(language, StatementLanguageStatus.ENABLED);
        problemFs.writeToFile(getStatementAvailableLanguagesFilePath(null, problemJid), writeObj(initialLanguage));

        String statementText = type == ProblemType.BUNDLE
                ? BundleProblemStatementUtils.getDefaultStatement(language)
                : ProgrammingProblemStatementUtils.getDefaultText(language);

        updateStatement(null, problemJid, language, new ProblemStatement.Builder()
                .title(ProblemStatementUtils.getDefaultTitle(language))
                .text(statementText)
                .build());
    }

    public ProblemStatement getStatement(String userJid, String problemJid, String language) {
        String title = problemFs.readFromFile(getStatementTitleFilePath(userJid, problemJid, language));
        String text = problemFs.readFromFile(getStatementTextFilePath(userJid, problemJid, language));

        return new ProblemStatement.Builder().title(title).text(text).build();
    }

    public void updateStatement(String userJid, String problemJid, String language, ProblemStatement statement) {
        problemFs.writeToFile(getStatementTitleFilePath(userJid, problemJid, language), statement.getTitle());
        problemFs.writeToFile(getStatementTextFilePath(userJid, problemJid, language), statement.getText());
    }

    public Map<String, StatementLanguageStatus> getStatementAvailableLanguages(String userJid, String problemJid) {
        String languages = problemFs.readFromFile(getStatementAvailableLanguagesFilePath(userJid, problemJid));
        try {
            return mapper.readValue(languages, new TypeReference<Map<String, StatementLanguageStatus>>() {});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Set<String> getStatementEnabledLanguages(String userJid, String problemJid) {
        return getStatementAvailableLanguages(userJid, problemJid)
                .entrySet()
                .stream()
                .filter(e -> e.getValue() == StatementLanguageStatus.ENABLED)
                .map(e -> e.getKey())
                .collect(Collectors.toSet());
    }

    public void addStatementLanguage(String userJid, String problemJid, String language) {
        Map<String, StatementLanguageStatus> availableLanguages = getStatementAvailableLanguages(userJid, problemJid);
        availableLanguages.put(language, StatementLanguageStatus.ENABLED);

        ProblemStatement statement = getStatement(userJid, problemJid, getStatementDefaultLanguage(userJid, problemJid));
        problemFs.writeToFile(getStatementTitleFilePath(userJid, problemJid, language), statement.getTitle());
        problemFs.writeToFile(getStatementTextFilePath(userJid, problemJid, language), statement.getText());
        problemFs.writeToFile(getStatementAvailableLanguagesFilePath(userJid, problemJid), writeObj(availableLanguages));
    }

    public void enableStatementLanguage(String userJid, String problemJid, String language) {
        Map<String, StatementLanguageStatus> availableLanguages = getStatementAvailableLanguages(userJid, problemJid);
        availableLanguages.put(language, StatementLanguageStatus.ENABLED);
        problemFs.writeToFile(getStatementAvailableLanguagesFilePath(userJid, problemJid), writeObj(availableLanguages));
    }

    public void disableStatementLanguage(String userJid, String problemJid, String language) {
        Map<String, StatementLanguageStatus> availableLanguages = getStatementAvailableLanguages(userJid, problemJid);
        availableLanguages.put(language, StatementLanguageStatus.DISABLED);
        problemFs.writeToFile(getStatementAvailableLanguagesFilePath(userJid, problemJid), writeObj(availableLanguages));
    }

    public void makeStatementDefaultLanguage(String userJid, String problemJid, String language) {
        problemFs.writeToFile(getStatementDefaultLanguageFilePath(userJid, problemJid), language);
    }

    public String getStatementDefaultLanguage(String userJid, String problemJid) {
        return problemFs.readFromFile(getStatementDefaultLanguageFilePath(userJid, problemJid));
    }

    public Map<String, String> getTitlesByLanguage(String userJid, String problemJid) {
        Map<String, StatementLanguageStatus> availableLanguages = getStatementAvailableLanguages(userJid, problemJid);

        ImmutableMap.Builder<String, String> titlesByLanguageBuilder = ImmutableMap.builder();

        for (Map.Entry<String, StatementLanguageStatus> entry : availableLanguages.entrySet()) {
            if (entry.getValue() == StatementLanguageStatus.ENABLED) {
                String title = problemFs.readFromFile(getStatementTitleFilePath(userJid, problemJid, entry.getKey()));
                titlesByLanguageBuilder.put(entry.getKey(), title);
            }
        }

        return titlesByLanguageBuilder.build();
    }

    public void uploadStatementMediaFile(String userJid, String problemJid, InputStream mediaFile, String filename) {
        Path mediaDirPath = getStatementMediaDirPath(userJid, problemJid);
        problemFs.uploadPublicFile(mediaDirPath.resolve(filename), mediaFile);
    }

    public void uploadStatementMediaFileZipped(String userJid, String problemJid, InputStream mediaFileZipped) {
        Path mediaDirPath = getStatementMediaDirPath(userJid, problemJid);
        problemFs.uploadZippedFiles(mediaDirPath, mediaFileZipped);
    }

    public List<FileInfo> getStatementMediaFiles(String userJid, String problemJid) {
        Path mediaDirPath = getStatementMediaDirPath(userJid, problemJid);
        return problemFs.listFilesInDirectory(mediaDirPath);
    }

    public String getStatementMediaFileURL(String userJid, String problemJid, String filename) {
        Path mediaFilePath = getStatementMediaDirPath(userJid, problemJid).resolve(filename);
        return problemFs.getPublicFileUrl(mediaFilePath);
    }

    public Set<ProblemSkeleton> getSkeletons(String userJid, String problemJid) {
        Set<ProblemSkeleton> skeletons = new HashSet<>();
        for (FileInfo file : getStatementMediaFiles(null, problemJid)) {
            if (file.getName().toLowerCase().startsWith("skeleton.")) {
                Path mediaFilePath = getStatementMediaDirPath(userJid, problemJid).resolve(file.getName());
                String extension = FilenameUtils.getExtension(file.getName());

                Set<String> languages = new HashSet<>();
                for (String language : GradingLanguageRegistry.getInstance().getLanguages().keySet()) {
                    if (GradingLanguageRegistry.getInstance().get(language).getAllowedExtensions().contains(extension)) {
                        languages.add(language);
                    }
                }

                skeletons.add(new ProblemSkeleton.Builder()
                        .languages(languages)
                        .content(problemFs.readByteArrayFromFile(mediaFilePath))
                        .build());
            }
        }
        return skeletons;
    }

    private Path getStatementsDirPath(String userJid, String problemJid) {
        return getRootDirPath(userJid, problemJid).resolve("statements");
    }

    private Path getStatementDirPath(String userJid, String problemJid, String language) {
        return getStatementsDirPath(userJid, problemJid).resolve(language);
    }

    private Path getStatementTitleFilePath(String userJid, String problemJid, String language) {
        return getStatementDirPath(userJid, problemJid, language).resolve("title.txt");
    }

    private Path getStatementTextFilePath(String userJid, String problemJid, String language) {
        return getStatementDirPath(userJid, problemJid, language).resolve("text.html");
    }

    private Path getStatementDefaultLanguageFilePath(String userJid, String problemJid) {
        return getStatementsDirPath(userJid, problemJid).resolve("defaultLanguage.txt");
    }

    private Path getStatementAvailableLanguagesFilePath(String userJid, String problemJid) {
        return getStatementsDirPath(userJid, problemJid).resolve("availableLanguages.txt");
    }

    private Path getStatementMediaDirPath(String userJid, String problemJid) {
        return getStatementsDirPath(userJid, problemJid).resolve("resources");
    }
}
