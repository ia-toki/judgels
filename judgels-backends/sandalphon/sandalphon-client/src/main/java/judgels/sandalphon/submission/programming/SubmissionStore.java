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
    List<Submission> getSubmissionsForScoreboard(String containerJid);
    Page<Submission> getSubmissionsForDownload(
            String containerJid,
            Optional<String> userJid,
            Optional<String> problemJid,
            Optional<Long> lastSubmissionId,
            Optional<Integer> limit);
    Page<Submission> getSubmissions(
            String containerJid,
            Optional<String> userJid,
            Optional<String> problemJid,
            Optional<Integer> page);

    long getTotalSubmissions(String containerJid, String userJid, String problemJid);
    Map<String, Long> getTotalSubmissionsMap(String containerJid, String userJid, Set<String> problemJids);

    Submission createSubmission(SubmissionData data, String gradingEngine);
    String createGrading(Submission submission);
    boolean updateGrading(String gradingJid, GradingResult result);
}
