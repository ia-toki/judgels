package org.iatoki.judgels.sandalphon.lesson;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.iatoki.judgels.FileInfo;
import org.iatoki.judgels.FileSystemProvider;
import org.iatoki.judgels.GitCommit;
import org.iatoki.judgels.GitProvider;
import org.iatoki.judgels.play.Page;
import org.iatoki.judgels.sandalphon.lesson.partner.LessonPartner;
import org.iatoki.judgels.sandalphon.lesson.partner.LessonPartnerConfig;
import org.iatoki.judgels.sandalphon.lesson.partner.LessonPartnerNotFoundException;
import org.iatoki.judgels.sandalphon.lesson.statement.LessonStatement;
import org.iatoki.judgels.sandalphon.SandalphonProperties;
import org.iatoki.judgels.sandalphon.StatementLanguageStatus;
import org.iatoki.judgels.sandalphon.lesson.partner.LessonPartnerDao;
import org.iatoki.judgels.sandalphon.lesson.partner.LessonPartnerModel;
import org.iatoki.judgels.sandalphon.lesson.partner.LessonPartnerModel_;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Singleton
public final class LessonServiceImpl implements LessonService {

    private final LessonDao lessonDao;
    private final FileSystemProvider lessonFileSystemProvider;
    private final GitProvider lessonGitProvider;
    private final LessonPartnerDao lessonPartnerDao;

    @Inject
    public LessonServiceImpl(LessonDao lessonDao, @LessonFileSystemProvider FileSystemProvider lessonFileSystemProvider, @LessonGitProvider GitProvider lessonGitProvider, LessonPartnerDao lessonPartnerDao) {
        this.lessonDao = lessonDao;
        this.lessonFileSystemProvider = lessonFileSystemProvider;
        this.lessonGitProvider = lessonGitProvider;
        this.lessonPartnerDao = lessonPartnerDao;
    }

    @Override
    public Lesson createLesson(String slug, String additionalNote, String initialLanguageCode, String userJid, String userIpAddress) throws IOException {
        LessonModel lessonModel = new LessonModel();
        lessonModel.slug = slug;
        lessonModel.additionalNote = additionalNote;

        lessonDao.persist(lessonModel, userJid, userIpAddress);

        initStatements(lessonModel.jid, initialLanguageCode);
        lessonFileSystemProvider.createDirectory(getClonesDirPath(lessonModel.jid));

        return createLessonFromModel(lessonModel);
    }

    @Override
    public boolean lessonExistsByJid(String lessonJid) {
        return lessonDao.existsByJid(lessonJid);
    }

    @Override
    public boolean lessonExistsBySlug(String slug) {
        return lessonDao.existsBySlug(slug);
    }

    @Override
    public Lesson findLessonById(long lessonId) throws LessonNotFoundException {
        LessonModel lessonModel = lessonDao.findById(lessonId);
        if (lessonModel == null) {
            throw new LessonNotFoundException("Lesson not found.");
        }

        return createLessonFromModel(lessonModel);
    }

    @Override
    public Lesson findLessonByJid(String lessonJid) {
        LessonModel lessonModel = lessonDao.findByJid(lessonJid);

        return createLessonFromModel(lessonModel);
    }

    @Override
    public boolean isUserPartnerForLesson(String lessonJid, String userJid) {
        return lessonPartnerDao.existsByLessonJidAndPartnerJid(lessonJid, userJid);
    }

    @Override
    public void createLessonPartner(String lessonJid, String userJid, LessonPartnerConfig config, String createUserJid, String createUserIpAddress) {
        LessonModel lessonModel = lessonDao.findByJid(lessonJid);

        LessonPartnerModel lessonPartnerModel = new LessonPartnerModel();
        lessonPartnerModel.lessonJid = lessonModel.jid;
        lessonPartnerModel.userJid = userJid;
        lessonPartnerModel.config = new Gson().toJson(config);

        lessonPartnerDao.persist(lessonPartnerModel, createUserJid, createUserIpAddress);

        lessonDao.edit(lessonModel, createUserJid, createUserIpAddress);
    }

    @Override
    public void updateLessonPartner(long lessonPartnerId, LessonPartnerConfig config, String userJid, String userIpAddress) {
        LessonPartnerModel lessonPartnerModel = lessonPartnerDao.findById(lessonPartnerId);
        lessonPartnerModel.config = new Gson().toJson(config);

        lessonPartnerDao.edit(lessonPartnerModel, userJid, userIpAddress);

        LessonModel lessonModel = lessonDao.findByJid(lessonPartnerModel.lessonJid);

        lessonDao.edit(lessonModel, userJid, userIpAddress);
    }

    @Override
    public Page<LessonPartner> getPageOfLessonPartners(String lessonJid, long pageIndex, long pageSize, String orderBy, String orderDir) {
        long totalRows = lessonPartnerDao.countByFiltersEq("", ImmutableMap.of(LessonPartnerModel_.lessonJid, lessonJid));
        List<LessonPartnerModel> lessonPartnerModels = lessonPartnerDao.findSortedByFiltersEq(orderBy, orderDir, "", ImmutableMap.of(LessonPartnerModel_.lessonJid, lessonJid), pageIndex, pageIndex * pageSize);
        List<LessonPartner> lessonPartners = Lists.transform(lessonPartnerModels, m -> createLessonPartnerFromModel(m));

        return new Page<>(lessonPartners, totalRows, pageIndex, pageSize);
    }

    @Override
    public LessonPartner findLessonPartnerById(long lessonPartnerId) throws LessonPartnerNotFoundException {
        LessonPartnerModel lessonPartnerModel = lessonPartnerDao.findById(lessonPartnerId);
        if (lessonPartnerModel == null) {
            throw new LessonPartnerNotFoundException("Lesson partner not found.");
        }

        return createLessonPartnerFromModel(lessonPartnerModel);
    }

    @Override
    public LessonPartner findLessonPartnerByLessonJidAndPartnerJid(String lessonJid, String partnerJid) {
        LessonPartnerModel lessonPartnerModel = lessonPartnerDao.findByLessonJidAndPartnerJid(lessonJid, partnerJid);

        return createLessonPartnerFromModel(lessonPartnerModel);
    }

    @Override
    public void updateLesson(String lessonJid, String slug, String additionalNote, String userJid, String userIpAddress) {
        LessonModel lessonModel = lessonDao.findByJid(lessonJid);
        lessonModel.slug = slug;
        lessonModel.additionalNote = additionalNote;

        lessonDao.edit(lessonModel, userJid, userIpAddress);
    }

    @Override
    public Page<Lesson> getPageOfLessons(long pageIndex, long pageSize, String orderBy, String orderDir, String filterString, String userJid, boolean isAdmin) {
        if (isAdmin) {
            long totalRows = lessonDao.countByFilters(filterString);
            List<LessonModel> lessonModels = lessonDao.findSortedByFilters(orderBy, orderDir, filterString, pageIndex * pageSize, pageSize);

            List<Lesson> lessons = Lists.transform(lessonModels, m -> createLessonFromModel(m));
            return new Page<>(lessons, totalRows, pageIndex, pageSize);
        } else {
            List<String> lessonJidsWhereIsAuthor = lessonDao.getJidsByAuthorJid(userJid);
            List<String> lessonJidsWhereIsPartner = lessonPartnerDao.getLessonJidsByPartnerJid(userJid);

            ImmutableSet.Builder<String> allowedLessonJidsBuilder = ImmutableSet.builder();
            allowedLessonJidsBuilder.addAll(lessonJidsWhereIsAuthor);
            allowedLessonJidsBuilder.addAll(lessonJidsWhereIsPartner);

            Set<String> allowedLessonJids = allowedLessonJidsBuilder.build();

            long totalRows = lessonDao.countByFiltersIn(filterString, ImmutableMap.of(LessonModel_.jid, allowedLessonJids));
            List<LessonModel> lessonModels = lessonDao.findSortedByFiltersIn(orderBy, orderDir, filterString, ImmutableMap.of(LessonModel_.jid, allowedLessonJids), pageIndex * pageSize, pageSize);

            List<Lesson> lessons = Lists.transform(lessonModels, m -> createLessonFromModel(m));
            return new Page<>(lessons, totalRows, pageIndex, pageSize);
        }

    }

    @Override
    public Map<String, StatementLanguageStatus> getAvailableLanguages(String userJid, String lessonJid) throws IOException {
        String langs = lessonFileSystemProvider.readFromFile(getStatementAvailableLanguagesFilePath(userJid, lessonJid));
        return new Gson().fromJson(langs, new TypeToken<Map<String, StatementLanguageStatus>>() { }.getType());
    }

    @Override
    public void addLanguage(String userJid, String lessonJid, String languageCode) throws IOException {
        String langs = lessonFileSystemProvider.readFromFile(getStatementAvailableLanguagesFilePath(userJid, lessonJid));
        Map<String, StatementLanguageStatus> availableLanguages = new Gson().fromJson(langs, new TypeToken<Map<String, StatementLanguageStatus>>() { }.getType());

        availableLanguages.put(languageCode, StatementLanguageStatus.ENABLED);

        LessonStatement defaultLanguageStatement = getStatement(userJid, lessonJid, getDefaultLanguage(userJid, lessonJid));
        lessonFileSystemProvider.writeToFile(getStatementTitleFilePath(userJid, lessonJid, languageCode), defaultLanguageStatement.getTitle());
        lessonFileSystemProvider.writeToFile(getStatementTextFilePath(userJid, lessonJid, languageCode), defaultLanguageStatement.getText());
        lessonFileSystemProvider.writeToFile(getStatementAvailableLanguagesFilePath(userJid, lessonJid), new Gson().toJson(availableLanguages));
    }

    @Override
    public void enableLanguage(String userJid, String lessonJid, String languageCode) throws IOException {
        String langs = lessonFileSystemProvider.readFromFile(getStatementAvailableLanguagesFilePath(userJid, lessonJid));
        Map<String, StatementLanguageStatus> availableLanguages = new Gson().fromJson(langs, new TypeToken<Map<String, StatementLanguageStatus>>() { }.getType());

        availableLanguages.put(languageCode, StatementLanguageStatus.ENABLED);

        lessonFileSystemProvider.writeToFile(getStatementAvailableLanguagesFilePath(userJid, lessonJid), new Gson().toJson(availableLanguages));
    }

    @Override
    public void disableLanguage(String userJid, String lessonJid, String languageCode) throws IOException {
        String langs = lessonFileSystemProvider.readFromFile(getStatementAvailableLanguagesFilePath(userJid, lessonJid));
        Map<String, StatementLanguageStatus> availableLanguages = new Gson().fromJson(langs, new TypeToken<Map<String, StatementLanguageStatus>>() { }.getType());

        availableLanguages.put(languageCode, StatementLanguageStatus.DISABLED);

        lessonFileSystemProvider.writeToFile(getStatementAvailableLanguagesFilePath(userJid, lessonJid), new Gson().toJson(availableLanguages));
    }

    @Override
    public void makeDefaultLanguage(String userJid, String lessonJid, String languageCode) throws IOException {
        lessonFileSystemProvider.writeToFile(getStatementDefaultLanguageFilePath(userJid, lessonJid), languageCode);
    }

    @Override
    public String getDefaultLanguage(String userJid, String lessonJid) throws IOException {
        return lessonFileSystemProvider.readFromFile(getStatementDefaultLanguageFilePath(userJid, lessonJid));
    }

    @Override
    public LessonStatement getStatement(String userJid, String lessonJid, String languageCode) throws IOException {
        String title = lessonFileSystemProvider.readFromFile(getStatementTitleFilePath(userJid, lessonJid, languageCode));
        String text = lessonFileSystemProvider.readFromFile(getStatementTextFilePath(userJid, lessonJid, languageCode));

        return new LessonStatement(title, text);
    }

    @Override
    public Map<String, String> getTitlesByLanguage(String userJid, String lessonJid) throws IOException {
        Map<String, StatementLanguageStatus> availableLanguages = getAvailableLanguages(userJid, lessonJid);

        ImmutableMap.Builder<String, String> titlesByLanguageBuilder = ImmutableMap.builder();

        for (Map.Entry<String, StatementLanguageStatus> entry : availableLanguages.entrySet()) {
            if (entry.getValue() == StatementLanguageStatus.ENABLED) {
                String title = lessonFileSystemProvider.readFromFile(getStatementTitleFilePath(userJid, lessonJid, entry.getKey()));
                titlesByLanguageBuilder.put(entry.getKey(), title);
            }
        }

        return titlesByLanguageBuilder.build();
    }

    @Override
    public void updateStatement(String userJid, String lessonJid, String languageCode, LessonStatement statement) throws IOException {
        LessonModel lessonModel = lessonDao.findByJid(lessonJid);
        lessonFileSystemProvider.writeToFile(getStatementTitleFilePath(userJid, lessonModel.jid, languageCode), statement.getTitle());
        lessonFileSystemProvider.writeToFile(getStatementTextFilePath(userJid, lessonModel.jid, languageCode), statement.getText());
    }

    @Override
    public void uploadStatementMediaFile(String userJid, String lessonJid, File mediaFile, String filename) throws IOException {
        LessonModel lessonModel = lessonDao.findByJid(lessonJid);
        List<String> mediaDirPath = getStatementMediaDirPath(userJid, lessonModel.jid);
        lessonFileSystemProvider.uploadFile(mediaDirPath, mediaFile, filename);
    }

    @Override
    public void uploadStatementMediaFileZipped(String userJid, String lessonJid, File mediaFileZipped) throws IOException {
        LessonModel lessonModel = lessonDao.findByJid(lessonJid);
        List<String> mediaDirPath = getStatementMediaDirPath(userJid, lessonModel.jid);
        lessonFileSystemProvider.uploadZippedFiles(mediaDirPath, mediaFileZipped, false);
    }

    @Override
    public List<FileInfo> getStatementMediaFiles(String userJid, String lessonJid) {
        List<String> mediaDirPath = getStatementMediaDirPath(userJid, lessonJid);
        return lessonFileSystemProvider.listFilesInDirectory(mediaDirPath);
    }

    @Override
    public String getStatementMediaFileURL(String userJid, String lessonJid, String filename) {
        List<String> mediaFilePath = appendPath(getStatementMediaDirPath(userJid, lessonJid), filename);
        return lessonFileSystemProvider.getURL(mediaFilePath);
    }

    @Override
    public List<GitCommit> getVersions(String userJid, String lessonJid) {
        List<String> root = getRootDirPath(lessonFileSystemProvider, userJid, lessonJid);
        return lessonGitProvider.getLog(root);
    }

    @Override
    public void initRepository(String userJid, String lessonJid) {
        List<String> root = getRootDirPath(lessonFileSystemProvider, null, lessonJid);

        lessonGitProvider.init(root);
        lessonGitProvider.addAll(root);
        lessonGitProvider.commit(root, userJid, "no@email.com", "Initial commit", "");
    }

    @Override
    public boolean userCloneExists(String userJid, String lessonJid) {
        List<String> root = getCloneDirPath(userJid, lessonJid);

        return lessonFileSystemProvider.directoryExists(root);
    }

    @Override
    public void createUserCloneIfNotExists(String userJid, String lessonJid) {
        List<String> origin = getOriginDirPath(lessonJid);
        List<String> root = getCloneDirPath(userJid, lessonJid);

        if (!lessonFileSystemProvider.directoryExists(root)) {
            lessonGitProvider.clone(origin, root);
        }
    }

    @Override
    public boolean commitThenMergeUserClone(String userJid, String lessonJid, String title, String description, String userIpAddress) {
        List<String> root = getCloneDirPath(userJid, lessonJid);

        lessonGitProvider.addAll(root);
        lessonGitProvider.commit(root, userJid, "no@email.com", title, description);
        boolean success = lessonGitProvider.rebase(root);

        if (!success) {
            lessonGitProvider.resetToParent(root);
        } else {
            LessonModel lessonModel = lessonDao.findByJid(lessonJid);
            lessonDao.edit(lessonModel, userJid, userIpAddress);
        }

        return success;
    }

    @Override
    public boolean updateUserClone(String userJid, String lessonJid) {
        List<String> root = getCloneDirPath(userJid, lessonJid);

        lessonGitProvider.addAll(root);
        lessonGitProvider.commit(root, userJid, "no@email.com", "dummy", "dummy");
        boolean success = lessonGitProvider.rebase(root);

        lessonGitProvider.resetToParent(root);

        return success;
    }

    @Override
    public boolean pushUserClone(String userJid, String lessonJid, String userIpAddress) {
        List<String> origin = getOriginDirPath(lessonJid);
        List<String> root = getRootDirPath(lessonFileSystemProvider, userJid, lessonJid);

        if (lessonGitProvider.push(root)) {
            lessonGitProvider.resetHard(origin);

            LessonModel lessonModel = lessonDao.findByJid(lessonJid);
            lessonDao.edit(lessonModel, userJid, userIpAddress);

            return true;
        }
        return false;
    }

    @Override
    public boolean fetchUserClone(String userJid, String lessonJid) {
        List<String> root = getRootDirPath(lessonFileSystemProvider, userJid, lessonJid);

        return lessonGitProvider.fetch(root);
    }

    @Override
    public void discardUserClone(String userJid, String lessonJid) throws IOException {
        List<String> root = getRootDirPath(lessonFileSystemProvider, userJid, lessonJid);

        lessonFileSystemProvider.removeFile(root);
    }

    @Override
    public void restore(String lessonJid, String hash, String userJid, String userIpAddress) {
        List<String> root = getOriginDirPath(lessonJid);

        lessonGitProvider.restore(root, hash);

        LessonModel lessonModel = lessonDao.findByJid(lessonJid);
        lessonDao.edit(lessonModel, userJid, userIpAddress);
    }

    private void initStatements(String lessonJid, String initialLanguageCode) throws IOException {
        List<String> statementsDirPath = getStatementsDirPath(null, lessonJid);
        lessonFileSystemProvider.createDirectory(statementsDirPath);

        List<String> statementDirPath = getStatementDirPath(null, lessonJid, initialLanguageCode);
        lessonFileSystemProvider.createDirectory(statementDirPath);

        List<String> mediaDirPath = getStatementMediaDirPath(null, lessonJid);
        lessonFileSystemProvider.createDirectory(mediaDirPath);
        lessonFileSystemProvider.createFile(appendPath(mediaDirPath, ".gitkeep"));

        lessonFileSystemProvider.createFile(getStatementTitleFilePath(null, lessonJid, initialLanguageCode));
        lessonFileSystemProvider.createFile(getStatementTextFilePath(null, lessonJid, initialLanguageCode));
        lessonFileSystemProvider.writeToFile(getStatementDefaultLanguageFilePath(null, lessonJid), initialLanguageCode);

        Map<String, StatementLanguageStatus> initialLanguage = ImmutableMap.of(initialLanguageCode, StatementLanguageStatus.ENABLED);
        lessonFileSystemProvider.writeToFile(getStatementAvailableLanguagesFilePath(null, lessonJid), new Gson().toJson(initialLanguage));
    }

    private List<String> getStatementsDirPath(String userJid, String lessonJid) {
        return appendPath(getRootDirPath(lessonFileSystemProvider, userJid, lessonJid), "statements");
    }

    private List<String> getStatementDirPath(String userJid, String lessonJid, String languageCode) {
        return appendPath(getStatementsDirPath(userJid, lessonJid), languageCode);
    }

    private List<String> getStatementTitleFilePath(String userJid, String lessonJid, String languageCode) {
        return appendPath(getStatementDirPath(userJid, lessonJid, languageCode), "title.txt");
    }

    private List<String> getStatementTextFilePath(String userJid, String lessonJid, String languageCode) {
        return appendPath(getStatementDirPath(userJid, lessonJid, languageCode), "text.html");
    }

    private List<String> getStatementDefaultLanguageFilePath(String userJid, String lessonJid) {
        return appendPath(getStatementsDirPath(userJid, lessonJid), "defaultLanguage.txt");
    }

    private List<String> getStatementAvailableLanguagesFilePath(String userJid, String lessonJid) {
        return appendPath(getStatementsDirPath(userJid, lessonJid), "availableLanguages.txt");
    }

    private List<String> getStatementMediaDirPath(String userJid, String lessonJid) {
        return appendPath(getStatementsDirPath(userJid, lessonJid), "resources");
    }

    private static  List<String> getOriginDirPath(String lessonJid) {
        return Lists.newArrayList(SandalphonProperties.getInstance().getBaseLessonsDirKey(), lessonJid);
    }

    private static  List<String> getClonesDirPath(String lessonJid) {
        return Lists.newArrayList(SandalphonProperties.getInstance().getBaseLessonClonesDirKey(), lessonJid);
    }

    private static  List<String> getCloneDirPath(String userJid, String lessonJid) {
        return appendPath(getClonesDirPath(lessonJid), userJid);
    }

    private static  List<String> getRootDirPath(FileSystemProvider fileSystemProvider, String userJid, String lessonJid) {
        List<String> origin =  getOriginDirPath(lessonJid);
        List<String> root = getCloneDirPath(userJid, lessonJid);

        if (userJid == null || !fileSystemProvider.directoryExists(root)) {
            return origin;
        } else {
            return root;
        }
    }

    private static  List<String> appendPath(List<String> parentPath, String child) {
        parentPath.add(child);
        return parentPath;
    }

    private static  Lesson createLessonFromModel(LessonModel lessonModel) {
        return new Lesson(lessonModel.id, lessonModel.jid, lessonModel.slug, lessonModel.userCreate, lessonModel.additionalNote, new Date(lessonModel.timeUpdate));
    }

    private static  LessonPartner createLessonPartnerFromModel(LessonPartnerModel lessonPartnerModel) {
        return new LessonPartner(lessonPartnerModel.id, lessonPartnerModel.lessonJid, lessonPartnerModel.userJid, lessonPartnerModel.config);
    }
}
