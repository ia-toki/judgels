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
import judgels.uriel.contest.contestant.ContestContestantStore;

public class ContestResource implements ContestService {
    private final ActorChecker actorChecker;
    private final ContestRoleChecker contestRoleChecker;
    private final ContestStore contestStore;
    private final ContestContestantStore contestantStore;

    @Inject
    public ContestResource(
            ActorChecker actorChecker,
            ContestRoleChecker contestRoleChecker,
            ContestStore contestStore,
            ContestContestantStore contestantStore) {

        this.actorChecker = actorChecker;
        this.contestRoleChecker = contestRoleChecker;
        this.contestStore = contestStore;
        this.contestantStore = contestantStore;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public Contest getContest(Optional<AuthHeader> authHeader, String contestJid) {
        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));

        checkAllowed(contestRoleChecker.canViewContest(actorJid, contest));
        return contest;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public Contest getContestBySlug(Optional<AuthHeader> authHeader, String contestSlug) {
        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestBySlug(contestSlug));

        checkAllowed(contestRoleChecker.canViewContest(actorJid, contest));
        return contest;
    }

    @Override
    @UnitOfWork
    public void startVirtualContest(AuthHeader authHeader, String contestJid) {
        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(contestRoleChecker.canStartVirtualContest(actorJid, contest));

        contestantStore.startVirtualContest(contestJid, actorJid);
    }

    @Override
    @UnitOfWork(readOnly = true)
    public Page<Contest> getContests(Optional<AuthHeader> authHeader, Optional<Integer> page) {
        String actorJid = actorChecker.check(authHeader);

        SelectionOptions.Builder options = new SelectionOptions.Builder().from(SelectionOptions.DEFAULT_PAGED);
        options.orderBy("beginTime");
        page.ifPresent(options::page);

        return contestStore.getContests(actorJid, options.build());
    }

    @Override
    @UnitOfWork(readOnly = true)
    public List<Contest> getActiveContests(Optional<AuthHeader> authHeader) {
        String actorJid = actorChecker.check(authHeader);

        SelectionOptions.Builder options = new SelectionOptions.Builder().from(SelectionOptions.DEFAULT_ALL);
        options.orderBy("beginTime");
        options.orderDir(OrderDir.ASC);

        return contestStore.getActiveContests(actorJid, options.build());
    }

    @Override
    @UnitOfWork(readOnly = true)
    public Page<Contest> getPastContests(Optional<AuthHeader> authHeader, Optional<Integer> page) {
        String actorJid = actorChecker.check(authHeader);

        SelectionOptions.Builder options = new SelectionOptions.Builder().from(SelectionOptions.DEFAULT_PAGED);
        options.orderBy("beginTime");
        page.ifPresent(options::page);

        return contestStore.getPastContests(actorJid, options.build());
    }

    @Override
    @UnitOfWork
    public Contest createContest(AuthHeader authHeader, ContestData contestData) {
        String actorJid = actorChecker.check(authHeader);
        checkAllowed(contestRoleChecker.canCreateContest(actorJid));

        return contestStore.createContest(contestData);
    }

    @Override
    @UnitOfWork(readOnly = true)
    public Contest getContestDescription(Optional<AuthHeader> authHeader, String contestJid) {
        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(contestRoleChecker.canViewContest(actorJid, contest));

        return contest;
    }
}
