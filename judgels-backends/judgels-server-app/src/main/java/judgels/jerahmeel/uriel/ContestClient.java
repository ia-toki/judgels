package judgels.jerahmeel.uriel;

import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import judgels.uriel.api.contest.ContestInfo;
import judgels.uriel.contest.ContestStore;

public class ContestClient {
    private final ContestStore contestStore;

    @Inject
    public ContestClient(ContestStore contestStore) {
        this.contestStore = contestStore;
    }

    public Map<String, ContestInfo> getContestsByJids(Set<String> contestJids) {
        return contestStore.getContestInfosByJids(contestJids);
    }

    public Map<String, String> translateSlugsToJids(Set<String> contestSlugs) {
        return contestStore.translateSlugsToJids(contestSlugs);
    }
}
