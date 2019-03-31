package judgels.uriel.api.dump;

import static judgels.service.ServiceUtils.checkAllowed;

import io.dropwizard.hibernate.UnitOfWork;
import javax.inject.Inject;
import judgels.jophiel.api.role.Role;
import judgels.jophiel.api.user.me.MyUserService;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.persistence.AdminRoleDao;
import judgels.uriel.role.AdminRoleStore;

public class DumpResource implements DumpService {
    private final AdminRoleStore adminRoleStore;
    private final ContestStore contestStore;
    private final ActorChecker actorChecker;
    private final AdminRoleDao adminRoleDao;
    private final MyUserService myUserService;

    @Inject
    public DumpResource(
            AdminRoleStore adminRoleStore,
            ContestStore contestStore,
            ActorChecker actorChecker,
            AdminRoleDao adminRoleDao,
            MyUserService myUserService) {

        this.adminRoleStore = adminRoleStore;
        this.contestStore = contestStore;
        this.actorChecker = actorChecker;
        this.adminRoleDao = adminRoleDao;
        this.myUserService = myUserService;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public UrielDump exportDump(AuthHeader authHeader) {
        Role role = myUserService.getMyRole(authHeader);
        String actorJid = actorChecker.check(authHeader);
        checkAllowed(role == Role.SUPERADMIN || adminRoleDao.isAdmin(actorJid));

        return new UrielDump.Builder()
                .admins(adminRoleStore.exportDumps())
                .contests(contestStore.exportDumps())
                .build();
    }

    @Override
    @UnitOfWork
    public void importDump(AuthHeader authHeader, UrielDump urielDump) {
        Role role = myUserService.getMyRole(authHeader);
        String actorJid = actorChecker.check(authHeader);
        checkAllowed(role == Role.SUPERADMIN || adminRoleDao.isAdmin(actorJid));

        urielDump.getAdmins().forEach(dump -> adminRoleStore.importDump(dump));
        urielDump.getContests().forEach(dump -> contestStore.importDump(dump));
    }
}
