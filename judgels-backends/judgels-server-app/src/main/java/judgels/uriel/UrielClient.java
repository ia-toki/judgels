package judgels.uriel;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.inject.Inject;
import judgels.sandalphon.api.submission.programming.Submission;
import judgels.sandalphon.submission.programming.SubmissionStore;
import judgels.uriel.api.contest.ContestInfo;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.submission.UrielSubmissionStore;

public class UrielClient {
    @Inject protected ContestStore contestStore;
    @Inject @UrielSubmissionStore protected SubmissionStore submissionStore;

    @Inject public UrielClient() {}

    public Map<String, ContestInfo> getContestsByJids(Collection<String> contestJids) {
        return contestStore.getContestInfosByJids(contestJids);
    }

    public Map<String, String> translateContestSlugsToJids(Collection<String> contestSlugs) {
        return contestStore.translateSlugsToJids(contestSlugs);
    }

    public List<Submission> getSubmissionsForStats(String contestJid, Optional<Long> lastSubmissionId, Optional<Integer> limit) {
        return submissionStore
                .getSubmissionsForStats(Optional.of(contestJid), lastSubmissionId, limit.orElse(100))
                .getPage();
    }
}
