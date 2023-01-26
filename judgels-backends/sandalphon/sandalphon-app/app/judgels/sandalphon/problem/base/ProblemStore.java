package judgels.sandalphon.problem.base;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import judgels.fs.FileInfo;
import judgels.fs.FileSystem;
import judgels.persistence.FilterOptions;
import judgels.persistence.JidGenerator;
import judgels.persistence.api.OrderDir;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;
import judgels.sandalphon.Git;
import judgels.sandalphon.GitCommit;
import judgels.sandalphon.api.problem.Problem;
import judgels.sandalphon.api.problem.ProblemEditorial;
import judgels.sandalphon.api.problem.ProblemSetterRole;
import judgels.sandalphon.api.problem.ProblemStatement;
import judgels.sandalphon.api.problem.ProblemType;
import judgels.sandalphon.api.problem.partner.ProblemPartner;
import judgels.sandalphon.api.problem.partner.ProblemPartnerChildConfig;
import judgels.sandalphon.api.problem.partner.ProblemPartnerConfig;
import judgels.sandalphon.persistence.ProblemDao;
import judgels.sandalphon.persistence.ProblemModel;
import judgels.sandalphon.persistence.ProblemPartnerDao;
import judgels.sandalphon.persistence.ProblemPartnerModel;
import judgels.sandalphon.persistence.ProblemPartnerModel_;
import judgels.sandalphon.persistence.ProblemSetterDao;
import judgels.sandalphon.persistence.ProblemSetterModel;
import judgels.sandalphon.resource.StatementLanguageStatus;

public class ProblemStore extends AbstractProblemStore {
    private final ObjectMapper mapper;
    private final ProblemDao problemDao;
    private final FileSystem problemFs;
    private final Git problemGit;
    private final ProblemSetterDao problemSetterDao;
    private final ProblemPartnerDao problemPartnerDao;

    @Inject
    public ProblemStore(
            ObjectMapper mapper,
            ProblemDao problemDao,
            @ProblemFs FileSystem problemFs,
            @ProblemGit Git problemGit,
            ProblemSetterDao problemSetterDao,
            ProblemPartnerDao problemPartnerDao) {

        super(mapper, problemFs);
        this.mapper = mapper;
        this.problemDao = problemDao;
        this.problemFs = problemFs;
        this.problemGit = problemGit;
        this.problemSetterDao = problemSetterDao;
        this.problemPartnerDao = problemPartnerDao;
    }

    public Problem createProblem(ProblemType type, String slug, String additionalNote, String initialLanguageCode) {
        ProblemModel model = new ProblemModel();
        model.slug = slug;
        model.additionalNote = additionalNote;

        problemDao.insertWithJid(JidGenerator.newChildJid(ProblemModel.class, type.ordinal()), model);

        initStatements(model.jid, initialLanguageCode);
        problemFs.createDirectory(getClonesDirPath(model.jid));

        return createProblemFromModel(model);
    }

    public boolean problemExistsByJid(String problemJid) {
        return problemDao.existsByJid(problemJid);
    }

    public boolean problemExistsBySlug(String slug) {
        return problemDao.existsBySlug(slug);
    }

    public Optional<Problem> findProblemById(long problemId) {
        return problemDao.select(problemId).map(ProblemStore::createProblemFromModel);
    }

    public Problem findProblemByJid(String problemJid) {
        ProblemModel model = problemDao.findByJid(problemJid);
        return createProblemFromModel(model);
    }

    public Problem findProblemBySlug(String slug) {
        ProblemModel model = problemDao.findBySlug(slug);
        return createProblemFromModel(model);
    }

    public Map<ProblemSetterRole, List<String>> findProblemSettersByProblemJid(String problemJid) {
        Map<ProblemSetterRole, List<String>> setters = Maps.newHashMap();
        for (ProblemSetterModel m : problemSetterDao.selectAllByProblemJid(problemJid)) {
            ProblemSetterRole role = ProblemSetterRole.valueOf(m.role);
            setters.putIfAbsent(role, Lists.newArrayList());
            setters.get(role).add(m.userJid);
        }
        return setters;
    }

    public void updateProblemSettersByProblemJidAndRole(String problemJid, ProblemSetterRole role, List<String> userJids) {
        problemSetterDao.selectAllByProblemJidAndRole(problemJid, role).forEach(problemSetterDao::delete);
        problemSetterDao.flush();

        for (String userJid : userJids) {
            ProblemSetterModel m = new ProblemSetterModel();
            m.problemJid = problemJid;
            m.userJid = userJid;
            m.role = role.name();
            problemSetterDao.insert(m);
        }
    }

    public boolean isUserPartnerForProblem(String problemJid, String userJid) {
        return problemPartnerDao.existsByProblemJidAndPartnerJid(problemJid, userJid);
    }

    public void createProblemPartner(String problemJid, String userJid, ProblemPartnerConfig baseConfig, ProblemPartnerChildConfig childConfig) {
        ProblemModel model = problemDao.findByJid(problemJid);

        ProblemPartnerModel partnerModel = new ProblemPartnerModel();
        partnerModel.problemJid = model.jid;
        partnerModel.userJid = userJid;

        try {
            partnerModel.baseConfig = mapper.writeValueAsString(baseConfig);
            partnerModel.childConfig = mapper.writeValueAsString(childConfig);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        problemPartnerDao.insert(partnerModel);
        problemDao.update(model);
    }

    public void updateProblemPartner(long problemPartnerId, ProblemPartnerConfig baseConfig, ProblemPartnerChildConfig childConfig) {
        ProblemPartnerModel partnerModel = problemPartnerDao.find(problemPartnerId);

        try {
            partnerModel.baseConfig = mapper.writeValueAsString(baseConfig);
            partnerModel.childConfig = mapper.writeValueAsString(childConfig);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        problemPartnerDao.update(partnerModel);

        ProblemModel model = problemDao.findByJid(partnerModel.problemJid);
        problemDao.update(model);
    }

    public Page<ProblemPartner> getPageOfProblemPartners(String problemJid, long pageIndex, String orderBy, String orderDir) {
        FilterOptions<ProblemPartnerModel> filterOptions = new FilterOptions.Builder<ProblemPartnerModel>()
                .putColumnsEq(ProblemPartnerModel_.problemJid, problemJid)
                .build();
        SelectionOptions selectionOptions = new SelectionOptions.Builder()
                .from(SelectionOptions.DEFAULT_PAGED)
                .page((int) pageIndex)
                .orderBy(orderBy)
                .orderDir(OrderDir.of(orderDir))
                .build();

        long totalCount = problemPartnerDao.selectCount(filterOptions);
        List<ProblemPartnerModel> models = problemPartnerDao.selectAll(filterOptions, selectionOptions);
        List<ProblemPartner> partners = Lists.transform(models, this::createProblemPartnerFromModel);

        return new Page.Builder<ProblemPartner>()
                .page(partners)
                .totalCount(totalCount)
                .pageIndex(selectionOptions.getPage())
                .pageSize(selectionOptions.getPageSize())
                .build();
    }

    public Optional<ProblemPartner> findProblemPartnerById(long problemPartnerId) {
        return problemPartnerDao.select(problemPartnerId).map(this::createProblemPartnerFromModel);
    }

    public ProblemPartner findProblemPartnerByProblemJidAndPartnerJid(String problemJid, String partnerJid) {
        ProblemPartnerModel model = problemPartnerDao.findByProblemJidAndPartnerJid(problemJid, partnerJid);
        return createProblemPartnerFromModel(model);
    }

    public void updateProblem(String problemJid, String slug, String additionalNote) {
        ProblemModel model = problemDao.findByJid(problemJid);
        model.slug = slug;
        model.additionalNote = additionalNote;

        problemDao.update(model);
    }

    public Map<String, StatementLanguageStatus> getStatementAvailableLanguages(String userJid, String problemJid) {
        String languages = problemFs.readFromFile(getStatementAvailableLanguagesFilePath(userJid, problemJid));
        try {
            return mapper.readValue(languages, new TypeReference<Map<String, StatementLanguageStatus>>() {});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    public ProblemStatement getStatement(String userJid, String problemJid, String language) {
        String title = problemFs.readFromFile(getStatementTitleFilePath(userJid, problemJid, language));
        String text = problemFs.readFromFile(getStatementTextFilePath(userJid, problemJid, language));

        return new ProblemStatement.Builder().title(title).text(text).build();
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

    public void updateStatement(String userJid, String problemJid, String language, ProblemStatement statement) {
        problemFs.writeToFile(getStatementTitleFilePath(userJid, problemJid, language), statement.getTitle());
        problemFs.writeToFile(getStatementTextFilePath(userJid, problemJid, language), statement.getText());
    }

    public void uploadStatementMediaFile(String userJid, String problemJid, File mediaFile, String filename) {
        Path mediaDirPath = getStatementMediaDirPath(userJid, problemJid);
        try {
            problemFs.uploadPublicFile(mediaDirPath.resolve(filename), new FileInputStream(mediaFile));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void uploadStatementMediaFileZipped(String userJid, String problemJid, File mediaFileZipped) {
        Path mediaDirPath = getStatementMediaDirPath(userJid, problemJid);
        problemFs.uploadZippedFiles(mediaDirPath, mediaFileZipped, false);
    }

    public List<FileInfo> getStatementMediaFiles(String userJid, String problemJid) {
        Path mediaDirPath = getStatementMediaDirPath(userJid, problemJid);
        return problemFs.listFilesInDirectory(mediaDirPath);
    }

    public String getStatementMediaFileURL(String userJid, String problemJid, String filename) {
        Path mediaFilePath = getStatementMediaDirPath(userJid, problemJid).resolve(filename);
        return problemFs.getPublicFileUrl(mediaFilePath);
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

    public void uploadEditorialMediaFile(String userJid, String problemJid, File mediaFile, String filename) {
        Path mediaDirPath = getEditorialMediaDirPath(userJid, problemJid);
        try {
            problemFs.uploadPublicFile(mediaDirPath.resolve(filename), new FileInputStream(mediaFile));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void uploadEditorialMediaFileZipped(String userJid, String problemJid, File mediaFileZipped) {
        Path mediaDirPath = getEditorialMediaDirPath(userJid, problemJid);
        problemFs.uploadZippedFiles(mediaDirPath, mediaFileZipped, false);
    }

    public List<FileInfo> getEditorialMediaFiles(String userJid, String problemJid) {
        Path mediaDirPath = getEditorialMediaDirPath(userJid, problemJid);
        return problemFs.listFilesInDirectory(mediaDirPath);
    }

    public String getEditorialMediaFileURL(String userJid, String problemJid, String filename) {
        Path mediaFilePath = getEditorialMediaDirPath(userJid, problemJid).resolve(filename);
        return problemFs.getPublicFileUrl(mediaFilePath);
    }

    public List<GitCommit> getVersions(String userJid, String problemJid) {
        Path root = getRootDirPath(userJid, problemJid);
        return problemGit.getLog(root);
    }

    public void initRepository(String userJid, String problemJid) {
        Path root = getRootDirPath(null, problemJid);

        problemGit.init(root);
        problemGit.addAll(root);
        problemGit.commit(root, userJid, "no@email.com", "Initial commit", "");
    }

    public boolean userCloneExists(String userJid, String problemJid) {
        Path root = getCloneDirPath(userJid, problemJid);

        return problemFs.directoryExists(root);
    }

    public void createUserCloneIfNotExists(String userJid, String problemJid) {
        Path origin = getOriginDirPath(problemJid);
        Path root = getCloneDirPath(userJid, problemJid);

        if (!problemFs.directoryExists(root)) {
            problemGit.clone(origin, root);
        }
    }

    public boolean commitThenMergeUserClone(String userJid, String problemJid, String title, String text) {
        Path root = getCloneDirPath(userJid, problemJid);

        problemGit.addAll(root);
        problemGit.commit(root, userJid, "no@email.com", title, text);
        boolean success = problemGit.rebase(root);

        if (!success) {
            problemGit.resetToParent(root);
        } else {
            ProblemModel model = problemDao.findByJid(problemJid);
            problemDao.update(model);
        }

        return success;
    }

    public boolean updateUserClone(String userJid, String problemJid) {
        Path root = getCloneDirPath(userJid, problemJid);

        problemGit.addAll(root);
        problemGit.commit(root, userJid, "no@email.com", "dummy", "dummy");
        boolean success = problemGit.rebase(root);

        problemGit.resetToParent(root);

        return success;
    }

    public boolean pushUserClone(String userJid, String problemJid) {
        Path origin = getOriginDirPath(problemJid);
        Path root = getRootDirPath(userJid, problemJid);

        if (problemGit.push(root)) {
            problemGit.resetHard(origin);

            ProblemModel model = problemDao.findByJid(problemJid);
            problemDao.update(model);

            return true;
        }
        return false;
    }

    public boolean fetchUserClone(String userJid, String problemJid) {
        Path root = getRootDirPath(userJid, problemJid);

        return problemGit.fetch(root);
    }

    public void discardUserClone(String userJid, String problemJid) {
        Path root = getRootDirPath(userJid, problemJid);

        problemFs.removeFile(root);
    }

    public void restore(String problemJid, String hash) {
        Path root = getOriginDirPath(problemJid);

        problemGit.restore(root, hash);

        ProblemModel model = problemDao.findByJid(problemJid);
        problemDao.update(model);
    }

    private void initStatements(String problemJid, String initialLanguageCode) {
        Path statementsDirPath = getStatementsDirPath(null, problemJid);
        problemFs.createDirectory(statementsDirPath);

        Path statementDirPath = getStatementDirPath(null, problemJid, initialLanguageCode);
        problemFs.createDirectory(statementDirPath);

        Path mediaDirPath = getStatementMediaDirPath(null, problemJid);
        problemFs.createDirectory(mediaDirPath);
        problemFs.createFile(mediaDirPath.resolve(".gitkeep"));

        problemFs.createFile(getStatementTitleFilePath(null, problemJid, initialLanguageCode));
        problemFs.createFile(getStatementTextFilePath(null, problemJid, initialLanguageCode));
        problemFs.writeToFile(getStatementDefaultLanguageFilePath(null, problemJid), initialLanguageCode);

        Map<String, StatementLanguageStatus> initialLanguage = ImmutableMap.of(initialLanguageCode, StatementLanguageStatus.ENABLED);
        problemFs.writeToFile(getStatementAvailableLanguagesFilePath(null, problemJid), writeObj(initialLanguage));
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

    private static ProblemType getProblemType(ProblemModel model) {
        if (model.jid.startsWith("JIDPROG")) {
            return ProblemType.PROGRAMMING;
        } else if (model.jid.startsWith("JIDBUND")) {
            return ProblemType.BUNDLE;
        } else {
            throw new IllegalStateException("Unknown problem type: " + model.jid);
        }
    }

    public static Problem createProblemFromModel(ProblemModel model) {
        return new Problem.Builder()
                .id(model.id)
                .jid(model.jid)
                .slug(model.slug)
                .additionalNote(model.additionalNote)
                .authorJid(model.createdBy)
                .lastUpdateTime(model.updatedAt)
                .type(getProblemType(model))
                .build();
    }

    private ProblemPartner createProblemPartnerFromModel(ProblemPartnerModel model) {
        try {
            return new ProblemPartner.Builder()
                    .id(model.id)
                    .problemJid(model.problemJid)
                    .userJid(model.userJid)
                    .baseConfig(mapper.readValue(model.baseConfig, ProblemPartnerConfig.class))
                    .childConfig(mapper.readValue(model.childConfig, ProblemPartnerChildConfig.class))
                    .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
