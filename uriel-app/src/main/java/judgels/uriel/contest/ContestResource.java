package judgels.uriel.contest;

import static judgels.service.ServiceUtils.checkFound;
import static judgels.uriel.contest.ContestHacks.checkAllowed;

import io.dropwizard.hibernate.UnitOfWork;
import javax.inject.Inject;
import judgels.persistence.api.Page;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestData;
import judgels.uriel.api.contest.ContestService;

public class ContestResource implements ContestService {
    private final ContestStore contestStore;

    @Inject
    public ContestResource(ContestStore contestStore) {
        this.contestStore = contestStore;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public Contest getContest(String contestJid) {
        return checkAllowed(checkFound(contestStore.findContestByJid(contestJid)));
    }

    @Override
    @UnitOfWork(readOnly = true)
    public Page<Contest> getContests(int page, int pageSize) {
        return contestStore.getContests(page, pageSize);
    }

    @Override
    @UnitOfWork
    public Contest createContest(ContestData contestData) {
        return contestStore.createContest(contestData);
    }
}
