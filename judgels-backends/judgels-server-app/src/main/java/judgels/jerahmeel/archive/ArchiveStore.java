package judgels.jerahmeel.archive;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.jerahmeel.api.archive.Archive;
import judgels.jerahmeel.api.archive.ArchiveCreateData;
import judgels.jerahmeel.api.archive.ArchiveErrors;
import judgels.jerahmeel.api.archive.ArchiveUpdateData;
import judgels.jerahmeel.persistence.ArchiveDao;
import judgels.jerahmeel.persistence.ArchiveModel;
import judgels.jerahmeel.persistence.ArchiveModel_;
import judgels.persistence.api.OrderDir;

public class ArchiveStore {
    private final ArchiveDao archiveDao;

    @Inject
    public ArchiveStore(ArchiveDao archiveDao) {
        this.archiveDao = archiveDao;
    }

    public List<Archive> getArchives() {
        return archiveDao
                .select()
                .orderBy(ArchiveModel_.CATEGORY, OrderDir.ASC)
                .orderBy(ArchiveModel_.NAME, OrderDir.ASC)
                .all()
                .stream()
                .filter(m -> m.parentJid != null)
                .filter(m -> !m.parentJid.equals(""))
                .map(ArchiveStore::fromModel)
                .collect(toList());
    }

    public Optional<Archive> getArchiveByJid(String archiveJid) {
        return archiveDao.selectByJid(archiveJid).map(ArchiveStore::fromModel);
    }

    public Optional<Archive> getArchiveBySlug(String archiveSlug) {
        return archiveDao.selectBySlug(archiveSlug).map(ArchiveStore::fromModel);
    }

    public Map<String, Archive> getArchivesByJids(Collection<String> archiveJids) {
        return archiveDao.selectByJids(archiveJids).values().stream()
                .collect(Collectors.toMap(m -> m.jid, ArchiveStore::fromModel));
    }

    public Archive createArchive(ArchiveCreateData data) {
        if (archiveDao.selectBySlug(data.getSlug()).isPresent()) {
            throw ArchiveErrors.slugAlreadyExists(data.getSlug());
        }

        ArchiveModel model = new ArchiveModel();
        model.parentJid = "JIDTEMP";
        model.slug = data.getSlug();
        model.name = data.getName();
        model.category = data.getCategory();
        model.description = data.getDescription().orElse("");
        return fromModel(archiveDao.insert(model));
    }

    public Archive updateArchive(String archiveJid, ArchiveUpdateData data) {
        ArchiveModel model = archiveDao.findByJid(archiveJid);
        if (data.getSlug().isPresent()) {
            String newSlug = data.getSlug().get();
            if (model.slug == null || !model.slug.equals(newSlug)) {
                if (archiveDao.selectBySlug(newSlug).isPresent()) {
                    throw ArchiveErrors.slugAlreadyExists(newSlug);
                }
            }
        }

        data.getSlug().ifPresent(slug -> model.slug = slug);
        data.getName().ifPresent(name -> model.name = name);
        data.getCategory().ifPresent(category -> model.category = category);
        data.getDescription().ifPresent(description -> model.description = description);
        return fromModel(archiveDao.update(model));
    }

    private static Archive fromModel(ArchiveModel model) {
        return new Archive.Builder()
                .id(model.id)
                .jid(model.jid)
                .slug(model.slug)
                .name(model.name)
                .description(model.description)
                .category(model.category)
                .build();
    }
}
