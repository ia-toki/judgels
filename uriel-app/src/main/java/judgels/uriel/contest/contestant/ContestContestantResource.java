package judgels.uriel.contest.contestant;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import judgels.persistence.api.Page;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.contestant.ContestContestant;
import judgels.uriel.api.contest.contestant.ContestContestantService;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.role.RoleChecker;

public class ContestContestantResource implements ContestContestantService {
    private final ActorChecker actorChecker;
    private final RoleChecker roleChecker;
    private final ContestStore contestStore;
    private final ContestContestantStore contestantStore;

    @Inject
    public ContestContestantResource(
            ActorChecker actorChecker,
            RoleChecker roleChecker,
            ContestStore contestStore,
            ContestContestantStore contestantStore) {
        this.actorChecker = actorChecker;
        this.roleChecker = roleChecker;
        this.contestStore = contestStore;
        this.contestantStore = contestantStore;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public Page<ContestContestant> getContestants(AuthHeader authHeader, String contestJid, Optional<Integer> page) {
        return null;
    }

    @Override
    @UnitOfWork
    public List<String> addContestants(AuthHeader authHeader, String contestJid, List<String> contestantJids) {
        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.findContestByJid(contestJid));
        checkAllowed(roleChecker.canAddContestants(actorJid, contest));

        return contestantStore.addContestants(contestJid, contestantJids);
    }
}
