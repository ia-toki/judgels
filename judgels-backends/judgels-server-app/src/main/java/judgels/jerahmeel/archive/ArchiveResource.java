package judgels.jerahmeel.archive;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.Optional;
import javax.inject.Inject;
import judgels.jerahmeel.api.archive.Archive;
import judgels.jerahmeel.api.archive.ArchiveCreateData;
import judgels.jerahmeel.api.archive.ArchiveService;
import judgels.jerahmeel.api.archive.ArchiveUpdateData;
import judgels.jerahmeel.api.archive.ArchivesResponse;
import judgels.jerahmeel.role.RoleChecker;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;

public class ArchiveResource implements ArchiveService {
    private final ActorChecker actorChecker;
    private final RoleChecker roleChecker;
    private final ArchiveStore archiveStore;

    @Inject
    public ArchiveResource(
            ActorChecker actorChecker,
            RoleChecker roleChecker,
            ArchiveStore archiveStore) {
        this.actorChecker = actorChecker;
        this.roleChecker = roleChecker;
        this.archiveStore = archiveStore;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ArchivesResponse getArchives(Optional<AuthHeader> authHeader) {
        actorChecker.check(authHeader);

        return new ArchivesResponse.Builder()
                .data(archiveStore.getArchives())
                .build();
    }

    @Override
    @UnitOfWork
    public Archive createArchive(AuthHeader authHeader, ArchiveCreateData data) {
        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.isAdmin(actorJid));

        return archiveStore.createArchive(data);
    }

    @Override
    @UnitOfWork
    public Archive updateArchive(AuthHeader authHeader, String archiveJid, ArchiveUpdateData data) {
        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.isAdmin(actorJid));

        return checkFound(archiveStore.updateArchive(archiveJid, data));
    }
}
