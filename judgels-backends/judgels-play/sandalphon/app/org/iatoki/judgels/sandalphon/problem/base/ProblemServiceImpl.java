package org.iatoki.judgels.sandalphon.problem.base;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.inject.Inject;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import judgels.fs.FileInfo;
import judgels.fs.FileSystem;
import judgels.persistence.api.Page;
import judgels.sandalphon.api.problem.Problem;
import judgels.sandalphon.api.problem.ProblemStatement;
import judgels.sandalphon.api.problem.ProblemType;
import org.iatoki.judgels.GitCommit;
import org.iatoki.judgels.GitProvider;
import org.iatoki.judgels.play.jid.JidService;
import org.iatoki.judgels.sandalphon.SandalphonProperties;
import org.iatoki.judgels.sandalphon.StatementLanguageStatus;
import org.iatoki.judgels.sandalphon.problem.base.partner.ProblemPartner;
import org.iatoki.judgels.sandalphon.problem.base.partner.ProblemPartnerChildConfig;
import org.iatoki.judgels.sandalphon.problem.base.partner.ProblemPartnerConfig;
import org.iatoki.judgels.sandalphon.problem.base.partner.ProblemPartnerDao;
import org.iatoki.judgels.sandalphon.problem.base.partner.ProblemPartnerModel;
import org.iatoki.judgels.sandalphon.problem.base.partner.ProblemPartnerModel_;
import org.iatoki.judgels.sandalphon.problem.base.partner.ProblemPartnerNotFoundException;

import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Singleton
public final class ProblemServiceImpl implements ProblemService {

    private final ProblemDao problemDao;
    private final FileSystem problemFs;
    private final GitProvider problemGitProvider;
    private final ProblemPartnerDao problemPartnerDao;

    @Inject
    public ProblemServiceImpl(ProblemDao problemDao, @ProblemFileSystemProvider FileSystem problemFs, @ProblemGitProvider GitProvider problemGitProvider, ProblemPartnerDao problemPartnerDao) {
        this.problemDao = problemDao;
        this.problemFs = problemFs;
        this.problemGitProvider = problemGitProvider;
        this.problemPartnerDao = problemPartnerDao;
    }

    @Override
    public Problem createProblem(ProblemType type, String slug, String additionalNote, String initialLanguageCode, String userJid, String userIpAddress) throws IOException {
        ProblemModel problemModel = new ProblemModel();
        problemModel.slug = slug;
        problemModel.additionalNote = additionalNote;

        problemDao.persist(problemModel, type.ordinal(), userJid, userIpAddress);

        initStatements(problemModel.jid, initialLanguageCode);
        problemFs.createDirectory(getClonesDirPath(problemModel.jid));

        return createProblemFromModel(problemModel);
    }

    @Override
    public boolean problemExistsByJid(String problemJid) {
        return problemDao.existsByJid(problemJid);
    }

    @Override
    public boolean problemExistsBySlug(String slug) {
        return problemDao.existsBySlug(slug);
    }

    @Override
    public Problem findProblemById(long problemId) throws ProblemNotFoundException {
        ProblemModel problemModel = problemDao.findById(problemId);
        if (problemModel == null) {
            throw new ProblemNotFoundException("Problem not found.");
        }

        return createProblemFromModel(problemModel);
    }

    @Override
    public Problem findProblemByJid(String problemJid) {
        ProblemModel problemModel = problemDao.findByJid(problemJid);

        return createProblemFromModel(problemModel);
    }

    @Override
    public Problem findProblemBySlug(String slug) {
        ProblemModel problemModel = problemDao.findBySlug(slug);

        return createProblemFromModel(problemModel);
    }

    @Override
    public boolean isUserPartnerForProblem(String problemJid, String userJid) {
        return problemPartnerDao.existsByProblemJidAndPartnerJid(problemJid, userJid);
    }

    @Override
    public void createProblemPartner(String problemJid, String userJid, ProblemPartnerConfig baseConfig, ProblemPartnerChildConfig childConfig, String createUserJid, String createUserIpAddress) {
        ProblemModel problemModel = problemDao.findByJid(problemJid);

        ProblemPartnerModel problemPartnerModel = new ProblemPartnerModel();
        problemPartnerModel.problemJid = problemModel.jid;
        problemPartnerModel.userJid = userJid;
        problemPartnerModel.baseConfig = new Gson().toJson(baseConfig);
        problemPartnerModel.childConfig = new Gson().toJson(childConfig);

        problemPartnerDao.persist(problemPartnerModel, createUserJid, createUserIpAddress);

        problemDao.edit(problemModel, createUserJid, createUserIpAddress);
    }

    @Override
    public void updateProblemPartner(long problemPartnerId, ProblemPartnerConfig baseConfig, ProblemPartnerChildConfig childConfig, String userJid, String userIpAddress) {
        ProblemPartnerModel problemPartnerModel = problemPartnerDao.findById(problemPartnerId);
        problemPartnerModel.baseConfig = new Gson().toJson(baseConfig);
        problemPartnerModel.childConfig = new Gson().toJson(childConfig);

        problemPartnerDao.edit(problemPartnerModel, userJid, userIpAddress);

        ProblemModel problemModel = problemDao.findByJid(problemPartnerModel.problemJid);

        problemDao.edit(problemModel, userJid, userIpAddress);
    }

    @Override
    public Page<ProblemPartner> getPageOfProblemPartners(String problemJid, long pageIndex, long pageSize, String orderBy, String orderDir) {
        long totalRows = problemPartnerDao.countByFiltersEq("", ImmutableMap.of(ProblemPartnerModel_.problemJid, problemJid));
        List<ProblemPartnerModel> problemPartnerModels = problemPartnerDao.findSortedByFiltersEq(orderBy, orderDir, "", ImmutableMap.of(ProblemPartnerModel_.problemJid, problemJid), pageIndex * pageSize, pageSize);
        List<ProblemPartner> problemPartners = Lists.transform(problemPartnerModels, m -> createProblemPartnerFromModel(m));

        return new Page.Builder<ProblemPartner>()
                .page(problemPartners)
                .totalCount(totalRows)
                .pageIndex(pageIndex)
                .pageSize(pageSize)
                .build();
    }

    @Override
    public ProblemPartner findProblemPartnerById(long problemPartnerId) throws ProblemPartnerNotFoundException {
        ProblemPartnerModel problemPartnerModel = problemPartnerDao.findById(problemPartnerId);
        if (problemPartnerModel == null) {
            throw new ProblemPartnerNotFoundException("Problem partner not found.");
        }

        return createProblemPartnerFromModel(problemPartnerModel);
    }

    @Override
    public ProblemPartner findProblemPartnerByProblemJidAndPartnerJid(String problemJid, String partnerJid) {
        ProblemPartnerModel problemPartnerModel = problemPartnerDao.findByProblemJidAndPartnerJid(problemJid, partnerJid);

        return createProblemPartnerFromModel(problemPartnerModel);
    }

    @Override
    public void updateProblem(String problemJid, String slug, String additionalNote, String userJid, String userIpAddress) {
        ProblemModel problemModel = problemDao.findByJid(problemJid);
        problemModel.slug = slug;
        problemModel.additionalNote = additionalNote;

        problemDao.edit(problemModel, userJid, userIpAddress);
    }

    @Override
    public Page<Problem> getPageOfProblems(long pageIndex, long pageSize, String orderBy, String orderDir, String filterString, String userJid, boolean isAdmin) {
        if (isAdmin) {
            long totalRows = problemDao.countByFilters(filterString);
            List<ProblemModel> problemModels = problemDao.findSortedByFilters(orderBy, orderDir, filterString, pageIndex * pageSize, pageSize);

            List<Problem> problems = Lists.transform(problemModels, m -> createProblemFromModel(m));
            return new Page.Builder<Problem>()
                    .page(problems)
                    .totalCount(totalRows)
                    .pageIndex(pageIndex)
                    .pageSize(pageSize)
                    .build();
        } else {
            List<String> problemJidsWhereIsAuthor = problemDao.getJidsByAuthorJid(userJid);
            List<String> problemJidsWhereIsPartner = problemPartnerDao.getProblemJidsByPartnerJid(userJid);

            ImmutableSet.Builder<String> allowedProblemJidsBuilder = ImmutableSet.builder();
            allowedProblemJidsBuilder.addAll(problemJidsWhereIsAuthor);
            allowedProblemJidsBuilder.addAll(problemJidsWhereIsPartner);

            Set<String> allowedProblemJids = allowedProblemJidsBuilder.build();

            long totalRows = problemDao.countByFiltersIn(filterString, ImmutableMap.of(ProblemModel_.jid, allowedProblemJids));
            List<ProblemModel> problemModels = problemDao.findSortedByFiltersIn(orderBy, orderDir, filterString, ImmutableMap.of(ProblemModel_.jid, allowedProblemJids), pageIndex * pageSize, pageSize);

            List<Problem> problems = Lists.transform(problemModels, m -> createProblemFromModel(m));
            return new Page.Builder<Problem>()
                    .page(problems)
                    .totalCount(totalRows)
                    .pageIndex(pageIndex)
                    .pageSize(pageSize)
                    .build();
        }

    }

    @Override
    public Map<String, StatementLanguageStatus> getAvailableLanguages(String userJid, String problemJid) throws IOException {
        String langs = problemFs.readFromFile(getStatementAvailableLanguagesFilePath(userJid, problemJid));

        return new Gson().fromJson(langs, new TypeToken<Map<String, StatementLanguageStatus>>() {
        }.getType());
    }

    @Override
    public void addLanguage(String userJid, String problemJid, String languageCode) throws IOException {
        String langs = problemFs.readFromFile(getStatementAvailableLanguagesFilePath(userJid, problemJid));
        Map<String, StatementLanguageStatus> availableLanguages = new Gson().fromJson(langs, new TypeToken<Map<String, StatementLanguageStatus>>() { }.getType());

        availableLanguages.put(languageCode, StatementLanguageStatus.ENABLED);

        ProblemStatement defaultLanguageStatement = getStatement(userJid, problemJid, getDefaultLanguage(userJid, problemJid));
        problemFs.writeToFile(getStatementTitleFilePath(userJid, problemJid, languageCode), defaultLanguageStatement.getTitle());
        problemFs.writeToFile(getStatementTextFilePath(userJid, problemJid, languageCode), defaultLanguageStatement.getText());
        problemFs.writeToFile(getStatementAvailableLanguagesFilePath(userJid, problemJid), new Gson().toJson(availableLanguages));
    }

    @Override
    public void enableLanguage(String userJid, String problemJid, String languageCode) throws IOException {
        String langs = problemFs.readFromFile(getStatementAvailableLanguagesFilePath(userJid, problemJid));
        Map<String, StatementLanguageStatus> availableLanguages = new Gson().fromJson(langs, new TypeToken<Map<String, StatementLanguageStatus>>() { }.getType());

        availableLanguages.put(languageCode, StatementLanguageStatus.ENABLED);

        problemFs.writeToFile(getStatementAvailableLanguagesFilePath(userJid, problemJid), new Gson().toJson(availableLanguages));
    }

    @Override
    public void disableLanguage(String userJid, String problemJid, String languageCode) throws IOException {
        String langs = problemFs.readFromFile(getStatementAvailableLanguagesFilePath(userJid, problemJid));
        Map<String, StatementLanguageStatus> availableLanguages = new Gson().fromJson(langs, new TypeToken<Map<String, StatementLanguageStatus>>() { }.getType());

        availableLanguages.put(languageCode, StatementLanguageStatus.DISABLED);

        problemFs.writeToFile(getStatementAvailableLanguagesFilePath(userJid, problemJid), new Gson().toJson(availableLanguages));
    }

    @Override
    public void makeDefaultLanguage(String userJid, String problemJid, String languageCode) throws IOException {
        problemFs.writeToFile(getStatementDefaultLanguageFilePath(userJid, problemJid), languageCode);
    }

    @Override
    public String getDefaultLanguage(String userJid, String problemJid) throws IOException {
        return problemFs.readFromFile(getStatementDefaultLanguageFilePath(userJid, problemJid));
    }

    @Override
    public ProblemStatement getStatement(String userJid, String problemJid, String languageCode) throws IOException {
        String title = problemFs.readFromFile(getStatementTitleFilePath(userJid, problemJid, languageCode));
        String text = problemFs.readFromFile(getStatementTextFilePath(userJid, problemJid, languageCode));

        return new ProblemStatement.Builder().title(title).text(text).build();
    }

    @Override
    public Map<String, String> getTitlesByLanguage(String userJid, String problemJid) throws IOException {
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

    @Override
    public void updateStatement(String userJid, String problemJid, String languageCode, ProblemStatement statement) throws IOException {
        ProblemModel problemModel = problemDao.findByJid(problemJid);
        problemFs.writeToFile(getStatementTitleFilePath(userJid, problemModel.jid, languageCode), statement.getTitle());
        problemFs.writeToFile(getStatementTextFilePath(userJid, problemModel.jid, languageCode), statement.getText());
    }

    @Override
    public void uploadStatementMediaFile(String userJid, String problemJid, File mediaFile, String filename) throws IOException {
        ProblemModel problemModel = problemDao.findByJid(problemJid);
        Path mediaDirPath = getStatementMediaDirPath(userJid, problemModel.jid);
        problemFs.uploadPublicFile(mediaDirPath.resolve(filename), new FileInputStream(mediaFile));
    }

    @Override
    public void uploadStatementMediaFileZipped(String userJid, String problemJid, File mediaFileZipped) throws IOException {
        ProblemModel problemModel = problemDao.findByJid(problemJid);
        Path mediaDirPath = getStatementMediaDirPath(userJid, problemModel.jid);
        problemFs.uploadZippedFiles(mediaDirPath, mediaFileZipped, false);
    }

    @Override
    public List<FileInfo> getStatementMediaFiles(String userJid, String problemJid) {
        Path mediaDirPath = getStatementMediaDirPath(userJid, problemJid);
        return problemFs.listFilesInDirectory(mediaDirPath);
    }

    @Override
    public String getStatementMediaFileURL(String userJid, String problemJid, String filename) {
        Path mediaFilePath = getStatementMediaDirPath(userJid, problemJid).resolve(filename);
        return problemFs.getPublicFileUrl(mediaFilePath);
    }

    @Override
    public List<GitCommit> getVersions(String userJid, String problemJid) {
        Path root = getRootDirPath(problemFs, userJid, problemJid);
        return problemGitProvider.getLog(root);
    }

    @Override
    public void initRepository(String userJid, String problemJid) {
        Path root = getRootDirPath(problemFs, null, problemJid);

        problemGitProvider.init(root);
        problemGitProvider.addAll(root);
        problemGitProvider.commit(root, userJid, "no@email.com", "Initial commit", "");
    }

    @Override
    public boolean userCloneExists(String userJid, String problemJid) {
        Path root = getCloneDirPath(userJid, problemJid);

        return problemFs.directoryExists(root);
    }

    @Override
    public void createUserCloneIfNotExists(String userJid, String problemJid) {
        Path origin = getOriginDirPath(problemJid);
        Path root = getCloneDirPath(userJid, problemJid);

        if (!problemFs.directoryExists(root)) {
            problemGitProvider.clone(origin, root);
        }
    }

    @Override
    public boolean commitThenMergeUserClone(String userJid, String problemJid, String title, String text, String userIpAddress) {
        Path root = getCloneDirPath(userJid, problemJid);

        problemGitProvider.addAll(root);
        problemGitProvider.commit(root, userJid, "no@email.com", title, text);
        boolean success = problemGitProvider.rebase(root);

        if (!success) {
            problemGitProvider.resetToParent(root);
        } else {
            ProblemModel problemModel = problemDao.findByJid(problemJid);

            problemDao.edit(problemModel, userJid, userIpAddress);
        }

        return success;
    }

    @Override
    public boolean updateUserClone(String userJid, String problemJid) {
        Path root = getCloneDirPath(userJid, problemJid);

        problemGitProvider.addAll(root);
        problemGitProvider.commit(root, userJid, "no@email.com", "dummy", "dummy");
        boolean success = problemGitProvider.rebase(root);

        problemGitProvider.resetToParent(root);

        return success;
    }

    @Override
    public boolean pushUserClone(String userJid, String problemJid, String userIpAddress) {
        Path origin = getOriginDirPath(problemJid);
        Path root = getRootDirPath(problemFs, userJid, problemJid);

        if (problemGitProvider.push(root)) {
            problemGitProvider.resetHard(origin);

            ProblemModel problemModel = problemDao.findByJid(problemJid);

            problemDao.edit(problemModel, userJid, userIpAddress);

            return true;
        }
        return false;
    }

    @Override
    public boolean fetchUserClone(String userJid, String problemJid) {
        Path root = getRootDirPath(problemFs, userJid, problemJid);

        return problemGitProvider.fetch(root);
    }

    @Override
    public void discardUserClone(String userJid, String problemJid) throws IOException {
        Path root = getRootDirPath(problemFs, userJid, problemJid);

        problemFs.removeFile(root);
    }

    @Override
    public void restore(String problemJid, String hash, String userJid, String userIpAddress) {
        Path root = getOriginDirPath(problemJid);

        problemGitProvider.restore(root, hash);

        ProblemModel problemModel = problemDao.findByJid(problemJid);

        problemDao.edit(problemModel, userJid, userIpAddress);
    }

    private void initStatements(String problemJid, String initialLanguageCode) throws IOException {
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
        problemFs.writeToFile(getStatementAvailableLanguagesFilePath(null, problemJid), new Gson().toJson(initialLanguage));
    }

    private Path getStatementsDirPath(String userJid, String problemJid) {
        return getRootDirPath(problemFs, userJid, problemJid).resolve("statements");
    }

    private Path getStatementDirPath(String userJid, String problemJid, String languageCode) {
        return getStatementsDirPath(userJid, problemJid).resolve(languageCode);
    }

    private Path getStatementTitleFilePath(String userJid, String problemJid, String languageCode) {
        return getStatementDirPath(userJid, problemJid, languageCode).resolve("title.txt");
    }

    private Path getStatementTextFilePath(String userJid, String problemJid, String languageCode) {
        return getStatementDirPath(userJid, problemJid, languageCode).resolve("text.html");
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

    private static ProblemType getProblemType(ProblemModel problemModel) {
        String prefix = JidService.getInstance().parsePrefix(problemModel.jid);

        if (prefix.equals("PROG")) {
            return ProblemType.PROGRAMMING;
        } else if (prefix.equals("BUND")) {
            return ProblemType.BUNDLE;
        } else {
            throw new IllegalStateException("Unknown problem type: " + prefix);
        }
    }

    private static Problem createProblemFromModel(ProblemModel problemModel) {
        return new Problem.Builder()
                .id(problemModel.id)
                .jid(problemModel.jid)
                .slug(problemModel.slug)
                .additionalNote(problemModel.additionalNote)
                .authorJid(problemModel.createdBy)
                .lastUpdateTime(problemModel.updatedAt)
                .type(getProblemType(problemModel))
                .build();
    }

    private static ProblemPartner createProblemPartnerFromModel(ProblemPartnerModel problemPartnerModel) {
        return new ProblemPartner(problemPartnerModel.id, problemPartnerModel.problemJid, problemPartnerModel.userJid, problemPartnerModel.baseConfig, problemPartnerModel.childConfig);
    }

    private static Path getOriginDirPath(String problemJid) {
        return Paths.get(SandalphonProperties.getInstance().getBaseProblemsDirKey(), problemJid);
    }

    private static Path getClonesDirPath(String problemJid) {
        return Paths.get(SandalphonProperties.getInstance().getBaseProblemClonesDirKey(), problemJid);
    }

    private static Path getCloneDirPath(String userJid, String problemJid) {
        return getClonesDirPath(problemJid).resolve(userJid);
    }

    private static Path getRootDirPath(FileSystem fs, String userJid, String problemJid) {
        Path origin = getOriginDirPath(problemJid);
        if (userJid == null) {
            return origin;
        }

        Path root = getCloneDirPath(userJid, problemJid);
        if (!fs.directoryExists(root)) {
            return origin;
        } else {
            return root;
        }
    }
}
