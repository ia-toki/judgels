package org.iatoki.judgels.sandalphon.problem.base;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import judgels.fs.FileInfo;
import judgels.fs.FileSystem;
import judgels.persistence.FilterOptions;
import judgels.persistence.JidGenerator;
import judgels.persistence.api.OrderDir;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;
import judgels.sandalphon.api.problem.Problem;
import judgels.sandalphon.api.problem.ProblemStatement;
import judgels.sandalphon.api.problem.ProblemType;
import judgels.sandalphon.api.problem.partner.ProblemPartner;
import judgels.sandalphon.api.problem.partner.ProblemPartnerChildConfig;
import judgels.sandalphon.api.problem.partner.ProblemPartnerConfig;
import org.iatoki.judgels.Git;
import org.iatoki.judgels.GitCommit;
import org.iatoki.judgels.play.jid.JidService;
import org.iatoki.judgels.sandalphon.StatementLanguageStatus;
import org.iatoki.judgels.sandalphon.problem.base.partner.ProblemPartnerDao;
import org.iatoki.judgels.sandalphon.problem.base.partner.ProblemPartnerModel;
import org.iatoki.judgels.sandalphon.problem.base.partner.ProblemPartnerModel_;

public class ProblemStore extends AbstractProblemStore {
    private final ObjectMapper mapper;
    private final ProblemDao problemDao;
    private final FileSystem problemFs;
    private final Git problemGit;
    private final ProblemPartnerDao problemPartnerDao;

    @Inject
    public ProblemStore(
            ObjectMapper mapper,
            ProblemDao problemDao,
            @ProblemFs FileSystem problemFs,
            @ProblemGit Git problemGit,
            ProblemPartnerDao problemPartnerDao) {

        super(mapper, problemFs);
        this.mapper = mapper;
        this.problemDao = problemDao;
        this.problemFs = problemFs;
        this.problemGit = problemGit;
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

    public Page<ProblemPartner> getPageOfProblemPartners(String problemJid, long pageIndex, long pageSize, String orderBy, String orderDir) {
        FilterOptions<ProblemPartnerModel> filterOptions = new FilterOptions.Builder<ProblemPartnerModel>()
                .putColumnsEq(ProblemPartnerModel_.problemJid, problemJid)
                .build();
        SelectionOptions selectionOptions = new SelectionOptions.Builder()
                .page((int) pageIndex + 1)
                .pageSize((int) pageSize)
                .orderBy(orderBy)
                .orderDir(OrderDir.of(orderDir))
                .build();

        long totalCount = problemPartnerDao.selectCount(filterOptions);
        List<ProblemPartnerModel> models = problemPartnerDao.selectAll(filterOptions, selectionOptions);
        List<ProblemPartner> partners = Lists.transform(models, this::createProblemPartnerFromModel);

        return new Page.Builder<ProblemPartner>()
                .page(partners)
                .totalCount(totalCount)
                .pageIndex(pageIndex)
                .pageSize(pageSize)
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

    public Page<Problem> getPageOfProblems(long pageIndex, long pageSize, String orderBy, String orderDir, String filterString, String userJid, boolean isAdmin) {
        FilterOptions<ProblemModel> filterOptions;
        SelectionOptions selectionOptions = new SelectionOptions.Builder()
                .page((int) pageIndex + 1)
                .pageSize((int) pageSize)
                .orderBy(orderBy)
                .orderDir(OrderDir.of(orderDir))
                .build();

        if (isAdmin) {
            filterOptions = new FilterOptions.Builder<ProblemModel>()
                    .putColumnsLike(ProblemModel_.slug, filterString)
                    .putColumnsLike(ProblemModel_.additionalNote, filterString)
                    .build();
        } else {
            List<String> problemJidsWhereIsAuthor = problemDao.getJidsByAuthorJid(userJid);
            List<String> problemJidsWhereIsPartner = problemPartnerDao.getProblemJidsByPartnerJid(userJid);

            ImmutableSet.Builder<String> allowedProblemJidsBuilder = ImmutableSet.builder();
            allowedProblemJidsBuilder.addAll(problemJidsWhereIsAuthor);
            allowedProblemJidsBuilder.addAll(problemJidsWhereIsPartner);

            Set<String> allowedProblemJids = allowedProblemJidsBuilder.build();

            filterOptions = new FilterOptions.Builder<ProblemModel>()
                    .putColumnsIn(ProblemModel_.jid, allowedProblemJids)
                    .putColumnsLike(ProblemModel_.slug, filterString)
                    .putColumnsLike(ProblemModel_.additionalNote, filterString)
                    .build();
        }

        long totalCount = problemDao.selectCount(filterOptions);
        List<ProblemModel> models = problemDao.selectAll(filterOptions, selectionOptions);

        List<Problem> problems = Lists.transform(models, ProblemStore::createProblemFromModel);
        return new Page.Builder<Problem>()
                .page(problems)
                .totalCount(totalCount)
                .pageIndex(pageIndex)
                .pageSize(pageSize)
                .build();
    }

    public Map<String, StatementLanguageStatus> getAvailableLanguages(String userJid, String problemJid) {
        String languages = problemFs.readFromFile(getStatementAvailableLanguagesFilePath(userJid, problemJid));
        try {
            return mapper.readValue(languages, new TypeReference<Map<String, StatementLanguageStatus>>() {});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addLanguage(String userJid, String problemJid, String language) {
        Map<String, StatementLanguageStatus> availableLanguages = getAvailableLanguages(userJid, problemJid);
        availableLanguages.put(language, StatementLanguageStatus.ENABLED);

        ProblemStatement statement = getStatement(userJid, problemJid, getDefaultLanguage(userJid, problemJid));
        problemFs.writeToFile(getStatementTitleFilePath(userJid, problemJid, language), statement.getTitle());
        problemFs.writeToFile(getStatementTextFilePath(userJid, problemJid, language), statement.getText());
        problemFs.writeToFile(getStatementAvailableLanguagesFilePath(userJid, problemJid), writeObj(availableLanguages));
    }

    public void enableLanguage(String userJid, String problemJid, String language) {
        Map<String, StatementLanguageStatus> availableLanguages = getAvailableLanguages(userJid, problemJid);
        availableLanguages.put(language, StatementLanguageStatus.ENABLED);
        problemFs.writeToFile(getStatementAvailableLanguagesFilePath(userJid, problemJid), writeObj(availableLanguages));
    }

    public void disableLanguage(String userJid, String problemJid, String language) {
        Map<String, StatementLanguageStatus> availableLanguages = getAvailableLanguages(userJid, problemJid);
        availableLanguages.put(language, StatementLanguageStatus.DISABLED);
        problemFs.writeToFile(getStatementAvailableLanguagesFilePath(userJid, problemJid), writeObj(availableLanguages));
    }

    public void makeDefaultLanguage(String userJid, String problemJid, String language) {
        problemFs.writeToFile(getStatementDefaultLanguageFilePath(userJid, problemJid), language);
    }

    public String getDefaultLanguage(String userJid, String problemJid) {
        return problemFs.readFromFile(getStatementDefaultLanguageFilePath(userJid, problemJid));
    }

    public ProblemStatement getStatement(String userJid, String problemJid, String language) {
        String title = problemFs.readFromFile(getStatementTitleFilePath(userJid, problemJid, language));
        String text = problemFs.readFromFile(getStatementTextFilePath(userJid, problemJid, language));

        return new ProblemStatement.Builder().title(title).text(text).build();
    }

    public Map<String, String> getTitlesByLanguage(String userJid, String problemJid) {
        Map<String, StatementLanguageStatus> availableLanguages = getAvailableLanguages(userJid, problemJid);

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

    private static ProblemType getProblemType(ProblemModel model) {
        String prefix = JidService.getInstance().parsePrefix(model.jid);

        if (prefix.equals("PROG")) {
            return ProblemType.PROGRAMMING;
        } else if (prefix.equals("BUND")) {
            return ProblemType.BUNDLE;
        } else {
            throw new IllegalStateException("Unknown problem type: " + prefix);
        }
    }

    private static Problem createProblemFromModel(ProblemModel model) {
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
