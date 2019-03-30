package judgels.uriel.dump;

import static judgels.service.ServiceUtils.checkAllowed;

import io.dropwizard.hibernate.UnitOfWork;
import javax.inject.Inject;
import judgels.jophiel.api.role.Role;
import judgels.jophiel.api.user.me.MyUserService;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.dump.DumpService;
import judgels.uriel.api.dump.UrielDump;

public class DumpResource implements DumpService {
    private final UrielDumpImporter urielDumpImporter;
    private final UrielDumpExporter urielDumpExporter;
    private final MyUserService myUserService;

    @Inject
    public DumpResource(
            UrielDumpImporter urielDumpImporter,
            UrielDumpExporter urielDumpExporter,
            MyUserService myUserService) {

        this.urielDumpImporter = urielDumpImporter;
        this.urielDumpExporter = urielDumpExporter;
        this.myUserService = myUserService;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public UrielDump exportDump(AuthHeader authHeader) {
        Role role = myUserService.getMyRole(authHeader);
        checkAllowed(role == Role.SUPERADMIN);

        return urielDumpExporter.exportDump();
    }

    @Override
    @UnitOfWork
    public void importDump(AuthHeader authHeader, UrielDump urielDump) {
        Role role = myUserService.getMyRole(authHeader);
        checkAllowed(role == Role.SUPERADMIN);

        urielDumpImporter.importDump(urielDump);
    }
}
