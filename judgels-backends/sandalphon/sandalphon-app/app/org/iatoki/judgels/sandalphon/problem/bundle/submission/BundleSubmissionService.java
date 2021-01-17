package org.iatoki.judgels.sandalphon.problem.bundle.submission;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import judgels.fs.FileSystem;
import judgels.persistence.api.Page;
import org.iatoki.judgels.sandalphon.problem.bundle.grading.BundleAnswer;
import play.data.DynamicForm;

public interface BundleSubmissionService {

    Optional<BundleSubmission> findBundleSubmissionById(long submissionId);

    BundleSubmission findBundleSubmissionByJid(String submissionJid);

    List<Instant> getAllBundleSubmissionsSubmitTime();

    List<BundleSubmission> getAllBundleSubmissions();

    List<BundleSubmission> getBundleSubmissionsWithGradingsByContainerJidAndProblemJidAndUserJid(String containerJid, String problemJid, String userJid);

    List<BundleSubmission> getBundleSubmissionsByJids(List<String> submissionJids);

    List<BundleSubmission> getBundleSubmissionsByFilters(String orderBy, String orderDir, String authorJid, String problemJid, String containerJid);

    Page<BundleSubmission> getPageOfBundleSubmissions(long pageIndex, long pageSize, String orderBy, String orderDir, String authorJid, String problemJid, String containerJid);

    String submit(String problemJid, String containerJid, BundleAnswer answer, String userJid, String userIpAddress);

    void regrade(String submissionJid, BundleAnswer answer, String userJid, String userIpAddress);

    void afterGrade(String gradingJid, BundleAnswer answer);

    void storeSubmissionFiles(FileSystem localFileSystemProvider, FileSystem remoteFileSystemProvider, String submissionJid, BundleAnswer answer);

    BundleAnswer createBundleAnswerFromNewSubmission(DynamicForm data, String languageCode);

    BundleAnswer createBundleAnswerFromPastSubmission(FileSystem localFileSystemProvider, FileSystem remoteFileSystemProvider, String submissionJid) throws IOException;
}
