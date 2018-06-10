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

public class ContestContestantResource implements ContestContestantService {
    private final ActorChecker actorChecker;
    private final ContestStore contestStore;
    private final ContestContestantRoleChecker contestantRoleChecker;
    private final ContestContestantStore contestantStore;

    @Inject
    public ContestContestantResource(
            ActorChecker actorChecker,
            ContestStore contestStore,
            ContestContestantRoleChecker contestantRoleChecker,
            ContestContestantStore contestantStore) {

        this.actorChecker = actorChecker;
        this.contestStore = contestStore;
        this.contestantRoleChecker = contestantRoleChecker;
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
        checkAllowed(contestantRoleChecker.canAddContestants(actorJid, contest));

        return contestantStore.addContestants(contestJid, contestantJids);
    }
}
