package judgels.jerahmeel.archive;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.jerahmeel.api.archive.Archive;
import judgels.jerahmeel.persistence.ArchiveDao;
import judgels.jerahmeel.persistence.ArchiveModel;

public class ArchiveStore {
    private final ArchiveDao archiveDao;

    @Inject
    public ArchiveStore(ArchiveDao archiveDao) {
        this.archiveDao = archiveDao;
    }

    public Optional<Archive> getArchiveBySlug(String archiveSlug) {
        return archiveDao.selectBySlug(archiveSlug).map(ArchiveStore::fromModel);
    }

    public Map<String, Archive> getArchivesByJids(Set<String> archiveJids) {
        return archiveDao.selectByJids(archiveJids).values().stream()
                .collect(Collectors.toMap(m -> m.jid, ArchiveStore::fromModel));
    }

    private static Archive fromModel(ArchiveModel model) {
        return new Archive.Builder()
                .id(model.id)
                .jid(model.jid)
                .slug(model.slug)
                .name(model.name)
                .description(model.description)
                .build();
    }
}
