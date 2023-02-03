package judgels.uriel.contest.module;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.Set;
import javax.inject.Inject;
import judgels.jophiel.user.UserClient;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.module.ContestModuleService;
import judgels.uriel.api.contest.module.ContestModuleType;
import judgels.uriel.api.contest.module.ContestModulesConfig;
import judgels.uriel.contest.ContestRoleChecker;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.contest.log.ContestLogger;

public class ContestModuleResource implements ContestModuleService {
    private final ActorChecker actorChecker;
    private final ContestRoleChecker contestRoleChecker;
    private final ContestStore contestStore;
    private final ContestLogger contestLogger;
    private final ContestModuleStore moduleStore;
    private final UserClient userClient;

    @Inject
    public ContestModuleResource(
            ActorChecker actorChecker,
            ContestRoleChecker contestRoleChecker,
            ContestStore contestStore,
            ContestLogger contestLogger,
            ContestModuleStore moduleStore,
            UserClient userClient) {

        this.actorChecker = actorChecker;
        this.contestRoleChecker = contestRoleChecker;
        this.contestStore = contestStore;
        this.contestLogger = contestLogger;
        this.moduleStore = moduleStore;
        this.userClient = userClient;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public Set<ContestModuleType> getModules(AuthHeader authHeader, String contestJid) {
        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));

        checkAllowed(contestRoleChecker.canView(actorJid, contest));

        contestLogger.log(contestJid, "OPEN_MODULES");

        return moduleStore.getEnabledModules(contest.getJid());
    }

    @Override
    @UnitOfWork
    public void enableModule(AuthHeader authHeader, String contestJid, ContestModuleType type) {
        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));

        checkAllowed(contestRoleChecker.canManage(actorJid, contest));
        moduleStore.enableModule(contest.getJid(), type);

        contestLogger.log(contestJid, "ENABLE_MODULE", type.name());
    }

    @Override
    @UnitOfWork
    public void disableModule(AuthHeader authHeader, String contestJid, ContestModuleType type) {
        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));

        checkAllowed(contestRoleChecker.canManage(actorJid, contest));
        moduleStore.disableModule(contest.getJid(), type);

        contestLogger.log(contestJid, "DISABLE_MODULE", type.name());
    }

    @Override
    @UnitOfWork
    public ContestModulesConfig getConfig(AuthHeader authHeader, String contestJid) {
        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));

        checkAllowed(contestRoleChecker.canSupervise(actorJid, contest));
        ContestModulesConfig config = moduleStore.getConfig(contest.getJid(), contest.getStyle());
        if (config.getEditorial().isPresent()) {
            config = new ContestModulesConfig.Builder()
                    .from(config)
                    .profilesMap(userClient.parseProfiles(config.getEditorial().get().getPreface().orElse("")))
                    .build();
        }
        return config;
    }

    @Override
    @UnitOfWork
    public void upsertConfig(AuthHeader authHeader, String contestJid, ContestModulesConfig config) {
        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));

        checkAllowed(contestRoleChecker.canManage(actorJid, contest));
        moduleStore.upsertConfig(contest.getJid(), config);

        contestLogger.log(contestJid, "UPDATE_MODULE_CONFIG");
    }
}
