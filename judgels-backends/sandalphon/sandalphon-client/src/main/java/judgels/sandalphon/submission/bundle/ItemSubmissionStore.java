package judgels.sandalphon.submission.bundle;

import java.util.List;
import java.util.Optional;
import judgels.persistence.api.Page;
import judgels.sandalphon.api.submission.bundle.Grading;
import judgels.sandalphon.api.submission.bundle.ItemSubmission;

public interface ItemSubmissionStore {
    Optional<ItemSubmission> getSubmissionByJid(String jid);

    Page<ItemSubmission> getSubmissions(
            String containerJid,
            Optional<String> createdBy,
            Optional<String> problemJid,
            Optional<Integer> page);

    ItemSubmission upsertSubmission(
            String containerJid,
            String problemJid,
            String itemJid,
            String answer,
            Grading grading,
            String userJid);

    void deleteSubmission(String containerJid, String problemJid, String itemJid, String userJid);

    List<ItemSubmission> getLatestSubmissionsByUserInContainer(String containerJid, String userJid);

    List<ItemSubmission> getLatestSubmissionsByUserForProblemInContainer(
            String containerJid,
            String problemJid,
            String userJid);

    List<ItemSubmission> getSubmissionsForScoreboard(String containerJid);

    List<ItemSubmission> markSubmissionsForRegrade(
            Optional<String> containerJid,
            Optional<String> userJid,
            Optional<String> problemJid,
            Optional<Integer> batchSize);

    void updateGrading(String submissionJid, Grading grading);
}
