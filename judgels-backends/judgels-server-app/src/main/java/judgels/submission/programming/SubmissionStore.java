package judgels.submission.programming;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import judgels.api.problem.programming.ProblemSubmissionConfig;
import judgels.api.submission.programming.Submission;
import judgels.api.submission.programming.SubmissionData;
import judgels.gabriel.api.GradingResult;
import judgels.persistence.api.CursorPage;
import judgels.persistence.api.Page;

public interface SubmissionStore {
    Optional<Submission> getSubmissionById(long submissionId);
    Optional<Submission> getSubmissionByJid(String submissionJid);
    List<Submission> getSubmissionsForScoreboard(
            String containerJid,
            boolean withGradingDetails,
            long lastSubmissionId);
    Page<Submission> getSubmissionsForDownload(
            Optional<String> containerJid,
            Optional<String> userJid,
            Optional<String> problemJid,
            Optional<Long> lastSubmissionId,
            int pageSize);
    Page<Submission> getSubmissionsForStats(
            Optional<String> containerJid,
            Optional<Long> lastSubmissionId,
            int pageSize);
    Page<Submission> getSubmissions(
            Optional<String> containerJid,
            Optional<String> userJid,
            Optional<String> problemJid,
            int pageNumber,
            int pageSize);
    CursorPage<Submission> getSubmissionsCursor(
            Optional<String> containerJid,
            Optional<String> userJid,
            Optional<String> problemJid,
            Optional<Long> beforeId,
            Optional<Long> afterId,
            int pageSize);
    List<Submission> getUserProblemSubmissions(
            String containerJid,
            String userJid,
            String problemJid);
    Optional<Submission> getLatestSubmission(
            Optional<String> containerJid,
            Optional<String> userJid,
            Optional<String> problemJid);

    long getTotalSubmissions(String containerJid, String userJid, String problemJid);
    Map<String, Long> getTotalSubmissionsMap(String containerJid, String userJid, Collection<String> problemJids);

    Submission createSubmission(SubmissionData data, ProblemSubmissionConfig config);
    void updateSubmissionGradingEngine(String submissionJid, String gradingEngine);
    String createGrading(Submission submission);
    Optional<Submission> updateGrading(String gradingJid, GradingResult result);
}
