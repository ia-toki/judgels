package org.iatoki.judgels.sandalphon.problem.base;

import com.google.inject.ImplementedBy;
import org.iatoki.judgels.FileInfo;
import org.iatoki.judgels.GitCommit;
import org.iatoki.judgels.play.Page;
import org.iatoki.judgels.sandalphon.problem.base.statement.ProblemStatement;
import org.iatoki.judgels.sandalphon.StatementLanguageStatus;
import org.iatoki.judgels.sandalphon.problem.base.partner.ProblemPartner;
import org.iatoki.judgels.sandalphon.problem.base.partner.ProblemPartnerChildConfig;
import org.iatoki.judgels.sandalphon.problem.base.partner.ProblemPartnerConfig;
import org.iatoki.judgels.sandalphon.problem.base.partner.ProblemPartnerNotFoundException;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@ImplementedBy(ProblemServiceImpl.class)
public interface ProblemService {

    Problem createProblem(ProblemType type, String slug, String additionalNote, String initialLanguageCode, String userJid, String userIpAddress) throws IOException;

    boolean problemExistsByJid(String problemJid);

    boolean problemExistsBySlug(String slug);

    Problem findProblemById(long problemId) throws ProblemNotFoundException;

    Problem findProblemByJid(String problemJid);

    Problem findProblemBySlug(String slug);

    boolean isUserPartnerForProblem(String problemJid, String userJid);

    void createProblemPartner(String problemJid, String userJid, ProblemPartnerConfig baseConfig, ProblemPartnerChildConfig childConfig, String createUserJid, String createUserIpAddress);

    void updateProblemPartner(long problemPartnerId, ProblemPartnerConfig baseConfig, ProblemPartnerChildConfig childConfig, String userJid, String userIpAddress);

    Page<ProblemPartner> getPageOfProblemPartners(String problemJid, long pageIndex, long pageSize, String orderBy, String orderDir);

    ProblemPartner findProblemPartnerById(long problemPartnerId) throws ProblemPartnerNotFoundException;

    ProblemPartner findProblemPartnerByProblemJidAndPartnerJid(String problemJid, String partnerJid);

    void updateProblem(String problemJid, String slug, String additionalNote, String userJid, String userIpAddress);

    Page<Problem> getPageOfProblems(long pageIndex, long pageSize, String orderBy, String orderDir, String filterString, String userJid, boolean isAdmin);

    Map<String, StatementLanguageStatus> getAvailableLanguages(String userJid, String problemJid) throws IOException;

    void addLanguage(String userJid, String problemJid, String languageCode) throws IOException;

    void enableLanguage(String userJid, String problemJid, String languageCode) throws IOException;

    void disableLanguage(String userJid, String problemJid, String languageCode) throws IOException;

    void makeDefaultLanguage(String userJid, String problemJid, String languageCode) throws IOException;

    String getDefaultLanguage(String userJid, String problemJid) throws IOException;

    ProblemStatement getStatement(String userJid, String problemJid, String languageCode) throws IOException;

    Map<String, String> getTitlesByLanguage(String userJid, String problemJid) throws IOException;

    void updateStatement(String userJid, String problemJid, String languageCode, ProblemStatement statement) throws IOException;

    void uploadStatementMediaFile(String userJid, String problemJid, File mediaFile, String filename) throws IOException;

    void uploadStatementMediaFileZipped(String userJid, String problemJid, File mediaFileZipped) throws IOException;

    List<FileInfo> getStatementMediaFiles(String userJid, String problemJid);

    String getStatementMediaFileURL(String userJid, String problemJid, String filename);

    List<GitCommit> getVersions(String userJid, String problemJid);

    void initRepository(String userJid, String problemJid);

    boolean userCloneExists(String userJid, String problemJid);

    void createUserCloneIfNotExists(String userJid, String problemJid);

    boolean commitThenMergeUserClone(String userJid, String problemJid, String title, String text, String userIpAddress);

    boolean updateUserClone(String userJid, String problemJid);

    boolean pushUserClone(String userJid, String problemJid, String userIpAddress);

    boolean fetchUserClone(String userJid, String problemJid);

    void discardUserClone(String userJid, String problemJid) throws IOException;

    void restore(String problemJid, String hash, String userJid, String userIpAddress);
}
