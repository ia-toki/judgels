package judgels.jerahmeel.uriel;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;
import judgels.jerahmeel.submission.UrielSubmissionStore;
import judgels.sandalphon.api.submission.programming.Submission;
import judgels.sandalphon.submission.programming.SubmissionStore;
import judgels.uriel.api.contest.ContestInfo;
import judgels.uriel.contest.ContestStore;

public class ContestClient {
    private final ContestStore contestStore;
    private final SubmissionStore submissionStore;

    @Inject
    public ContestClient(
            ContestStore contestStore,
            @UrielSubmissionStore SubmissionStore submissionStore) {

        this.contestStore = contestStore;
        this.submissionStore = submissionStore;
    }

    public Map<String, ContestInfo> getContestsByJids(Set<String> contestJids) {
        return contestStore.getContestInfosByJids(contestJids);
    }

    public Map<String, String> translateSlugsToJids(Set<String> contestSlugs) {
        return contestStore.translateSlugsToJids(contestSlugs);
    }

    public List<Submission> getSubmissionsForStats(String contestJid, Optional<Long> lastSubmissionId, Optional<Integer> limit) {
        return submissionStore
                .getSubmissionsForStats(Optional.of(contestJid), lastSubmissionId, limit.orElse(100))
                .getPage();
    }
}
