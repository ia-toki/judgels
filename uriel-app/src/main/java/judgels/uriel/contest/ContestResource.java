package judgels.uriel.contest;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import judgels.persistence.api.OrderDir;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;
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
    public Contest getContest(Optional<AuthHeader> authHeader, String contestJid) {
        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.findContestByJid(contestJid));

        checkAllowed(roleChecker.canViewContest(actorJid, contest));
        return contest;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public Contest getContestById(Optional<AuthHeader> authHeader, long contestId) {
        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.findContestById(contestId));

        checkAllowed(roleChecker.canViewContest(actorJid, contest));
        return contest;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public Page<Contest> getContests(Optional<AuthHeader> authHeader, Optional<Integer> page) {
        String actorJid = actorChecker.check(authHeader);

        SelectionOptions.Builder options = new SelectionOptions.Builder();
        options.orderBy("beginTime");
        options.orderDir(OrderDir.DESC);
        page.ifPresent(options::page);

        return contestStore.getContests(actorJid, options.build());
    }

    @Override
    @UnitOfWork(readOnly = true)
    public List<Contest> getActiveContests(Optional<AuthHeader> authHeader) {
        String actorJid = actorChecker.check(authHeader);

        SelectionOptions.Builder options = new SelectionOptions.Builder();
        options.orderBy("beginTime");
        options.orderDir(OrderDir.DESC);

        return contestStore.getActiveContests(actorJid, options.build());
    }

    @Override
    @UnitOfWork(readOnly = true)
    public Page<Contest> getPastContests(Optional<AuthHeader> authHeader, Optional<Integer> page) {
        String actorJid = actorChecker.check(authHeader);

        SelectionOptions.Builder options = new SelectionOptions.Builder();
        options.orderBy("beginTime");
        options.orderDir(OrderDir.DESC);
        page.ifPresent(options::page);

        return contestStore.getPastContests(actorJid, options.build());
    }

    @Override
    @UnitOfWork
    public Contest createContest(AuthHeader authHeader, ContestData contestData) {
        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.canCreateContest(actorJid));

        return contestStore.createContest(contestData);
    }
}
