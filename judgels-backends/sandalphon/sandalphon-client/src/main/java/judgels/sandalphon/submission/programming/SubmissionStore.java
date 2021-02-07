package judgels.sandalphon.submission.programming;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import judgels.gabriel.api.GradingResult;
import judgels.persistence.api.Page;
import judgels.sandalphon.api.submission.programming.Submission;
import judgels.sandalphon.api.submission.programming.SubmissionData;

public interface SubmissionStore {
    Optional<Submission> getSubmissionById(long submissionId);
    Optional<Submission> getSubmissionByJid(String submissionJid);
    List<Submission> getSubmissionByJids(List<String> submissionJids);
    List<Submission> getSubmissionsForScoreboard(
            String containerJid,
            boolean withGradingDetails,
            long lastSubmissionId);
    Page<Submission> getSubmissionsForDownload(
            Optional<String> containerJid,
            Optional<String> userJid,
            Optional<String> problemJid,
            Optional<Long> lastSubmissionId,
            Optional<Integer> limit);
    Page<Submission> getSubmissionsForStats(
            Optional<String> containerJid,
            Optional<Long> lastSubmissionId,
            int limit);
    Page<Submission> getSubmissions(
            Optional<String> containerJid,
            Optional<String> userJid,
            Optional<String> problemJid,
            Optional<Integer> page);
    Optional<Submission> getLatestSubmission(
            Optional<String> containerJid,
            Optional<String> userJid,
            Optional<String> problemJid);

    long getTotalSubmissions(String containerJid, String userJid, String problemJid);
    Map<String, Long> getTotalSubmissionsMap(String containerJid, String userJid, Set<String> problemJids);

    Submission createSubmission(SubmissionData data, String gradingEngine);
    String createGrading(Submission submission);
    Optional<Submission> updateGrading(String gradingJid, GradingResult result);
}
