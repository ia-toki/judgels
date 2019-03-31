package judgels.uriel.api.dump;

import static judgels.service.ServiceUtils.checkAllowed;

import io.dropwizard.hibernate.UnitOfWork;
import javax.inject.Inject;
import judgels.jophiel.api.role.Role;
import judgels.jophiel.api.user.me.MyUserService;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.persistence.AdminRoleDao;

public class DumpResource implements DumpService {
    private final UrielDumpImporter urielDumpImporter;
    private final UrielDumpExporter urielDumpExporter;
    private final ActorChecker actorChecker;
    private final AdminRoleDao adminRoleDao;
    private final MyUserService myUserService;

    @Inject
    public DumpResource(
            UrielDumpImporter urielDumpImporter,
            UrielDumpExporter urielDumpExporter,
            ActorChecker actorChecker,
            AdminRoleDao adminRoleDao,
            MyUserService myUserService) {

        this.urielDumpImporter = urielDumpImporter;
        this.urielDumpExporter = urielDumpExporter;
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

        return urielDumpExporter.exportDump();
    }

    @Override
    @UnitOfWork
    public void importDump(AuthHeader authHeader, UrielDump urielDump) {
        Role role = myUserService.getMyRole(authHeader);
        String actorJid = actorChecker.check(authHeader);
        checkAllowed(role == Role.SUPERADMIN || adminRoleDao.isAdmin(actorJid));

        urielDumpImporter.importDump(urielDump);
    }
}
