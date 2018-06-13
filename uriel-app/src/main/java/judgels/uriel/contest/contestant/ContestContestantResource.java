package judgels.uriel.contest.contestant;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import judgels.jophiel.api.user.UserInfo;
import judgels.jophiel.api.user.UserService;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.contestant.ContestContestantService;
import judgels.uriel.api.contest.contestant.ContestContestantState;
import judgels.uriel.api.contest.contestant.ContestContestantsResponse;
import judgels.uriel.contest.ContestStore;

public class ContestContestantResource implements ContestContestantService {
    private final ActorChecker actorChecker;
    private final ContestStore contestStore;
    private final ContestContestantRoleChecker contestantRoleChecker;
    private final ContestContestantStore contestantStore;
    private final UserService userService;

    @Inject
    public ContestContestantResource(
            ActorChecker actorChecker,
            ContestStore contestStore,
            ContestContestantRoleChecker contestantRoleChecker,
            ContestContestantStore contestantStore,
            UserService userService) {

        this.actorChecker = actorChecker;
        this.contestStore = contestStore;
        this.contestantRoleChecker = contestantRoleChecker;
        this.contestantStore = contestantStore;
        this.userService = userService;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ContestContestantsResponse getContestants(AuthHeader authHeader, String contestJid) {
        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.findContestByJid(contestJid));
        checkAllowed(contestantRoleChecker.canGetContestants(actorJid, contest));

        Set<String> userJids = contestantStore.getContestants(contestJid);
        Map<String, UserInfo> usersMap = userService.findUsersByJids(userJids);
        Map<String, String> userCountriesMap = userService.findUserCountriesByJids(userJids);

        return new ContestContestantsResponse.Builder()
                .data(userJids)
                .usersMap(usersMap)
                .userCountriesMap(userCountriesMap)
                .build();
    }

    @Override
    @UnitOfWork
    public void register(AuthHeader authHeader, String contestJid) {
        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.findContestByJid(contestJid));
        checkAllowed(contestantRoleChecker.canRegister(actorJid, contest));

        contestantStore.upsertContestant(contestJid, actorJid);
    }

    @Override
    @UnitOfWork
    public void unregister(AuthHeader authHeader, String contestJid) {
        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.findContestByJid(contestJid));
        checkAllowed(contestantRoleChecker.canUnregister(actorJid, contest));

        contestantStore.removeContestant(contestJid, actorJid);
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ContestContestantState getMyContestantState(AuthHeader authHeader, String contestJid) {
        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.findContestByJid(contestJid));

        return contestantRoleChecker.getContestantState(actorJid, contest);
    }

    @Override
    @UnitOfWork
    public void addContestants(AuthHeader authHeader, String contestJid, Set<String> userJids) {
        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.findContestByJid(contestJid));
        checkAllowed(contestantRoleChecker.canSuperviseContestants(actorJid, contest));

        userJids.forEach(jid -> contestantStore.upsertContestant(contestJid, jid));
    }
}
