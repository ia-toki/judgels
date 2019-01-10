package org.iatoki.judgels.sandalphon.lesson;

import com.google.inject.ImplementedBy;
import org.iatoki.judgels.FileInfo;
import org.iatoki.judgels.GitCommit;
import org.iatoki.judgels.play.Page;
import org.iatoki.judgels.sandalphon.lesson.partner.LessonPartner;
import org.iatoki.judgels.sandalphon.lesson.partner.LessonPartnerConfig;
import org.iatoki.judgels.sandalphon.lesson.partner.LessonPartnerNotFoundException;
import org.iatoki.judgels.sandalphon.lesson.statement.LessonStatement;
import org.iatoki.judgels.sandalphon.StatementLanguageStatus;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@ImplementedBy(LessonServiceImpl.class)
public interface LessonService {

    Lesson createLesson(String slug, String additionalNote, String initialLanguageCode, String userJid, String userIpAddress) throws IOException;

    boolean lessonExistsByJid(String lessonJid);

    boolean lessonExistsBySlug(String slug);

    Lesson findLessonById(long lessonId) throws LessonNotFoundException;

    Lesson findLessonByJid(String lessonJid);

    boolean isUserPartnerForLesson(String lessonJid, String userJid);

    void createLessonPartner(String lessonJid, String userJid, LessonPartnerConfig config, String createUserJid, String createUserIpAddress);

    void updateLessonPartner(long lessonPartnerId, LessonPartnerConfig config, String userJid, String userIpAddress);

    Page<LessonPartner> getPageOfLessonPartners(String lessonJid, long pageIndex, long pageSize, String orderBy, String orderDir);

    LessonPartner findLessonPartnerById(long lessonPartnerId) throws LessonPartnerNotFoundException;

    LessonPartner findLessonPartnerByLessonJidAndPartnerJid(String lessonJid, String partnerJid);

    void updateLesson(String lessonJid, String slug, String additionalNote, String userJid, String userIpAddress);

    Page<Lesson> getPageOfLessons(long pageIndex, long pageSize, String orderBy, String orderDir, String filterString, String userJid, boolean isAdmin);

    Map<String, StatementLanguageStatus> getAvailableLanguages(String userJid, String lessonJid) throws IOException;

    void addLanguage(String userJid, String lessonJid, String languageCode) throws IOException;

    void enableLanguage(String userJid, String lessonJid, String languageCode) throws IOException;

    void disableLanguage(String userJid, String lessonJid, String languageCode) throws IOException;

    void makeDefaultLanguage(String userJid, String lessonJid, String languageCode) throws IOException;

    String getDefaultLanguage(String userJid, String lessonJid) throws IOException;

    LessonStatement getStatement(String userJid, String lessonJid, String languageCode) throws IOException;

    Map<String, String> getTitlesByLanguage(String userJid, String lessonJid) throws IOException;

    void updateStatement(String userJid, String lessonJid, String languageCode, LessonStatement statement) throws IOException;

    void uploadStatementMediaFile(String userJid, String lessonJid, File mediaFile, String filename) throws IOException;

    void uploadStatementMediaFileZipped(String userJid, String lessonJid, File mediaFileZipped) throws IOException;

    List<FileInfo> getStatementMediaFiles(String userJid, String lessonJid);

    String getStatementMediaFileURL(String userJid, String lessonJid, String filename);

    List<GitCommit> getVersions(String userJid, String lessonJid);

    void initRepository(String userJid, String lessonJid);

    boolean userCloneExists(String userJid, String lessonJid);

    void createUserCloneIfNotExists(String userJid, String lessonJid);

    boolean commitThenMergeUserClone(String userJid, String lessonJid, String title, String description, String userIpAddress);

    boolean updateUserClone(String userJid, String lessonJid);

    boolean pushUserClone(String userJid, String lessonJid, String userIpAddress);

    boolean fetchUserClone(String userJid, String lessonJid);

    void discardUserClone(String userJid, String lessonJid) throws IOException;

    void restore(String lessonJid, String hash, String userJid, String userIpAddress);
}
