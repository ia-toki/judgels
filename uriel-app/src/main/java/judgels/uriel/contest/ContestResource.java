package judgels.uriel.contest;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import javax.inject.Inject;
import judgels.persistence.api.Page;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestData;
import judgels.uriel.api.contest.ContestService;
import judgels.uriel.role.RoleChecker;

public class ContestResource implements ContestService {
    private final ActorChecker actorChecker;
    private final RoleChecker roleChecker;
    private final ContestStore contestStore;

    @Inject
    public ContestResource(
            ActorChecker actorChecker,
            RoleChecker roleChecker,
            ContestStore contestStore) {
        this.actorChecker = actorChecker;
        this.roleChecker = roleChecker;
        this.contestStore = contestStore;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public Contest getContest(AuthHeader authHeader, String contestJid) {
        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.canReadContest(actorJid, contestJid));

        return checkFound(contestStore.findContestByJid(contestJid));
    }

    @Override
    @UnitOfWork(readOnly = true)
    public Page<Contest> getContests(AuthHeader authHeader, int page, int pageSize) {
        String actorJid = actorChecker.check(authHeader);
        return contestStore.getContests(actorJid, page, pageSize);
    }

    @Override
    @UnitOfWork
    public Contest createContest(ContestData contestData) {
        return contestStore.createContest(contestData);
    }
}
