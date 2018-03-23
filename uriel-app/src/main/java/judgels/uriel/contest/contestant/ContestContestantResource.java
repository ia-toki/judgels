package judgels.uriel.contest.contestant;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.ImmutableSet;
import io.dropwizard.hibernate.UnitOfWork;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.jophiel.api.user.UserService;
import judgels.persistence.api.Page;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.contest.ContestContestant;
import judgels.uriel.api.contest.contestant.ContestContestantService;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.role.RoleChecker;

public class ContestContestantResource implements ContestContestantService {
    private final ActorChecker actorChecker;
    private final RoleChecker roleChecker;
    private final ContestStore contestStore;
    private final ContestContestantStore contestantStore;
    private final UserService userService;

    @Inject
    public ContestContestantResource(
            ActorChecker actorChecker,
            RoleChecker roleChecker,
            ContestStore contestStore,
            ContestContestantStore contestantStore,
            UserService userService) {
        this.actorChecker = actorChecker;
        this.roleChecker = roleChecker;
        this.contestStore = contestStore;
        this.contestantStore = contestantStore;
        this.userService = userService;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public Page<ContestContestant> getContestants(AuthHeader authHeader, String contestJid, int page, int pageSize) {
        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.canReadContest(actorJid, contestJid));

        Page<String> contestantJids = contestantStore.getContestantJids(contestJid, page, pageSize);
        return contestantJids.mapData(jids ->
                userService.findUsersByJids(ImmutableSet.copyOf(jids)).values().stream()
                        .map(user -> new ContestContestant.Builder().contestant(user).build())
                        .collect(Collectors.toList()));
    }

    @Override
    @UnitOfWork
    public List<String> addContestants(AuthHeader authHeader, String contestJid, List<String> contestantJids) {
        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.canAddContestants(actorJid));
        checkFound(contestStore.findContestByJid(contestJid));

        return contestantStore.addContestants(contestJid, contestantJids);
    }

}
