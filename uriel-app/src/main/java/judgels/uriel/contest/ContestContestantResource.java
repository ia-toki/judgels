package judgels.uriel.contest;

import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.Set;
import javax.inject.Inject;
import judgels.persistence.api.Page;
import judgels.uriel.api.contest.ContestContestantService;

public class ContestContestantResource implements ContestContestantService {
    private final ContestContestantStore contestantStore;

    @Inject
    public ContestContestantResource(ContestContestantStore contestantStore) {
        this.contestantStore = contestantStore;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public Page<String> getContestants(String contestJid, int page, int pageSize) {
        return checkFound(contestantStore.getContestantJids(contestJid, page, pageSize));
    }

    @Override
    @UnitOfWork
    public Set<String> addContestants(String contestJid, Set<String> contestantJids) {
        return checkFound(contestantStore.addContestants(contestJid, contestantJids));
    }

}
