package judgels.uriel.contest.contestant;

import static judgels.service.ServiceUtils.checkFound;
import static judgels.uriel.contest.ContestHacks.checkAllowed;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.Set;
import javax.inject.Inject;
import judgels.persistence.api.Page;
import judgels.uriel.api.contest.contestant.ContestContestantService;
import judgels.uriel.contest.ContestStore;

public class ContestContestantResource implements ContestContestantService {
    private final ContestStore contestStore;
    private final ContestContestantStore contestantStore;

    @Inject
    public ContestContestantResource(ContestStore contestStore, ContestContestantStore contestantStore) {
        this.contestStore = contestStore;
        this.contestantStore = contestantStore;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public Page<String> getContestants(String contestJid, int page, int pageSize) {
        checkAllowed(checkFound(contestStore.findContestByJid(contestJid)));

        return contestantStore.getContestantJids(contestJid, page, pageSize);
    }

    @Override
    @UnitOfWork
    public Set<String> addContestants(String contestJid, Set<String> contestantJids) {
        checkAllowed(checkFound(contestStore.findContestByJid(contestJid)));

        return contestantStore.addContestants(contestJid, contestantJids);
    }

}
