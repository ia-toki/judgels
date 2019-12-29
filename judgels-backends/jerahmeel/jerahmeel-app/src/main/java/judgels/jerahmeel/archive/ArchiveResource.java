package judgels.jerahmeel.archive;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.Optional;
import javax.inject.Inject;
import judgels.jerahmeel.api.archive.ArchiveService;
import judgels.jerahmeel.api.archive.ArchivesResponse;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;

public class ArchiveResource implements ArchiveService {
    private final ActorChecker actorChecker;
    private final ArchiveStore archiveStore;

    @Inject
    public ArchiveResource(ActorChecker actorChecker, ArchiveStore archiveStore) {
        this.actorChecker = actorChecker;
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
}
