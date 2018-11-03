package judgels.uriel.contest;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.Optional;
import javax.inject.Inject;
import judgels.persistence.api.Page;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.contest.ActiveContestsResponse;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestConfig;
import judgels.uriel.api.contest.ContestCreateData;
import judgels.uriel.api.contest.ContestDescription;
import judgels.uriel.api.contest.ContestService;
import judgels.uriel.api.contest.ContestUpdateData;
import judgels.uriel.api.contest.ContestsResponse;
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

        checkAllowed(contestRoleChecker.canView(actorJid, contest));
        return contest;
    }

    @Override
    @UnitOfWork
    public Contest updateContest(AuthHeader authHeader, String contestJid, ContestUpdateData data) {
        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));

        checkAllowed(contestRoleChecker.canManage(actorJid, contest));
        return checkFound(contestStore.updateContest(contestJid, data));
    }

    @Override
    @UnitOfWork(readOnly = true)
    public Contest getContestBySlug(Optional<AuthHeader> authHeader, String contestSlug) {
        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestBySlug(contestSlug));

        checkAllowed(contestRoleChecker.canView(actorJid, contest));
        return contest;
    }

    @Override
    @UnitOfWork
    public void startVirtualContest(AuthHeader authHeader, String contestJid) {
        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(contestRoleChecker.canStartVirtual(actorJid, contest));

        contestantStore.startVirtualContest(contestJid, actorJid);
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ContestsResponse getContests(Optional<AuthHeader> authHeader, Optional<Integer> page) {
        String actorJid = actorChecker.check(authHeader);

        Page<Contest> contests = contestStore.getContests(actorJid, page);
        boolean canAdminister = contestRoleChecker.canAdminister(actorJid);
        ContestConfig config =  new ContestConfig.Builder()
                .canAdminister(canAdminister)
                .build();

        return new ContestsResponse.Builder()
                .data(contests)
                .config(config)
                .build();
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ActiveContestsResponse getActiveContests(Optional<AuthHeader> authHeader) {
        String actorJid = actorChecker.check(authHeader);
        return new ActiveContestsResponse.Builder()
                .data(contestStore.getActiveContests(actorJid))
                .build();
    }

    @Override
    @UnitOfWork
    public Contest createContest(AuthHeader authHeader, ContestCreateData data) {
        String actorJid = actorChecker.check(authHeader);
        checkAllowed(contestRoleChecker.canAdminister(actorJid));

        return contestStore.createContest(data);
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ContestDescription getContestDescription(Optional<AuthHeader> authHeader, String contestJid) {
        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(contestRoleChecker.canView(actorJid, contest));

        return checkFound(contestStore.getContestDescription(contest.getJid()));
    }

    @Override
    @UnitOfWork
    public ContestDescription updateContestDescription(
            AuthHeader authHeader,
            String contestJid,
            ContestDescription description) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(contestRoleChecker.canManage(actorJid, contest));

        return checkFound(contestStore.updateContestDescription(contest.getJid(), description));
    }
}
