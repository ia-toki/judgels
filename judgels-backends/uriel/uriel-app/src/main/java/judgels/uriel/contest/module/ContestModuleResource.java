package judgels.uriel.contest.module;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.Set;
import javax.inject.Inject;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.module.ContestModuleService;
import judgels.uriel.api.contest.module.ContestModuleType;
import judgels.uriel.contest.ContestRoleChecker;
import judgels.uriel.contest.ContestStore;

public class ContestModuleResource implements ContestModuleService {
    private final ActorChecker actorChecker;
    private final ContestRoleChecker contestRoleChecker;
    private final ContestStore contestStore;
    private final ContestModuleStore moduleStore;

    @Inject
    public ContestModuleResource(
            ActorChecker actorChecker,
            ContestRoleChecker contestRoleChecker,
            ContestStore contestStore,
            ContestModuleStore moduleStore) {

        this.actorChecker = actorChecker;
        this.contestRoleChecker = contestRoleChecker;
        this.contestStore = contestStore;
        this.moduleStore = moduleStore;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public Set<ContestModuleType> getModules(AuthHeader authHeader, String contestJid) {
        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));

        checkAllowed(contestRoleChecker.canViewContest(actorJid, contest));
        return moduleStore.getEnabledModules(contest.getJid());
    }

    @Override
    @UnitOfWork
    public void enableModule(AuthHeader authHeader, String contestJid, ContestModuleType type) {
        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));

        checkAllowed(contestRoleChecker.canEditContest(actorJid, contest));
        moduleStore.enableModule(contest.getJid(), type);
    }

    @Override
    @UnitOfWork
    public void disableModule(AuthHeader authHeader, String contestJid, ContestModuleType type) {
        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));

        checkAllowed(contestRoleChecker.canEditContest(actorJid, contest));
        moduleStore.disableModule(contest.getJid(), type);
    }
}
