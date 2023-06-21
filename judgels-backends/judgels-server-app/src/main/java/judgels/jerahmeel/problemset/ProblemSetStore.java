package judgels.jerahmeel.problemset;

import static java.util.stream.Collectors.toMap;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;
import judgels.jerahmeel.api.problemset.ProblemSet;
import judgels.jerahmeel.api.problemset.ProblemSetCreateData;
import judgels.jerahmeel.api.problemset.ProblemSetErrors;
import judgels.jerahmeel.api.problemset.ProblemSetUpdateData;
import judgels.jerahmeel.persistence.ArchiveDao;
import judgels.jerahmeel.persistence.ArchiveModel;
import judgels.jerahmeel.persistence.ProblemContestDao;
import judgels.jerahmeel.persistence.ProblemContestModel;
import judgels.jerahmeel.persistence.ProblemSetDao;
import judgels.jerahmeel.persistence.ProblemSetDao.ProblemSetQueryBuilder;
import judgels.jerahmeel.persistence.ProblemSetModel;
import judgels.jerahmeel.persistence.ProblemSetModel_;
import judgels.jerahmeel.persistence.ProblemSetProblemDao;
import judgels.jerahmeel.persistence.ProblemSetProblemModel;
import judgels.persistence.api.OrderDir;
import judgels.persistence.api.Page;

public class ProblemSetStore {
    private final ProblemSetDao problemSetDao;
    private final ProblemSetProblemDao problemSetProblemDao;
    private final ProblemContestDao problemContestDao;
    private final ArchiveDao archiveDao;

    @Inject
    public ProblemSetStore(
            ProblemSetDao problemSetDao,
            ProblemSetProblemDao problemSetProblemDao,
            ProblemContestDao problemContestDao,
            ArchiveDao archiveDao) {

        this.problemSetDao = problemSetDao;
        this.problemSetProblemDao = problemSetProblemDao;
        this.problemContestDao = problemContestDao;
        this.archiveDao = archiveDao;
    }

    public Optional<ProblemSet> getProblemSetByJid(String problemSetJid) {
        return problemSetDao.selectByJid(problemSetJid).map(ProblemSetStore::fromModel);
    }

    public Optional<ProblemSet> getProblemSetBySlug(String problemSetSlug) {
        return problemSetDao.selectBySlug(problemSetSlug).map(ProblemSetStore::fromModel);
    }

    public Map<String, ProblemSet> getProblemSetsBySlugs(Set<String> problemSetSlugs) {
        return problemSetDao.selectAllBySlugs(problemSetSlugs).stream().collect(toMap(
                m -> m.slug, ProblemSetStore::fromModel));
    }

    public Optional<ProblemSet> getProblemSetByContestJid(String contestJid) {
        for (ProblemContestModel pcm : problemContestDao.selectAllByContestJid(contestJid)) {
            for (ProblemSetProblemModel pspm : problemSetProblemDao.selectAllByProblemJid(pcm.problemJid)) {
                Optional<ProblemSetModel> m = problemSetDao.selectByJid(pspm.problemSetJid);
                if (m.isPresent()) {
                    return Optional.of(fromModel(m.get()));
                }
            }
        }
        return Optional.empty();
    }

    public Page<ProblemSet> getProblemSets(Optional<String> archiveJid, Optional<String> nameFilter, int pageNumber, int pageSize) {
        ProblemSetQueryBuilder query = problemSetDao.select();

        if (archiveJid.isPresent()) {
            query.whereArchiveIs(archiveJid.get());
        }
        if (nameFilter.isPresent()) {
            query.whereNameLike(nameFilter.get());
        }

        if (!nameFilter.orElse("").isEmpty() || archiveJid.isPresent()) {
            query.orderBy(ProblemSetModel_.CONTEST_TIME, OrderDir.DESC);
            query.orderBy(ProblemSetModel_.NAME, OrderDir.DESC);
        }

        return query
                .paged(pageNumber, pageSize)
                .mapPage(p -> Lists.transform(p, ProblemSetStore::fromModel));
    }

    public Map<String, String> getProblemSetNamesByJids(Set<String> problemSetJids) {
        return problemSetDao.selectByJids(problemSetJids)
                .values()
                .stream()
                .collect(toMap(
                        c -> c.jid,
                        c -> c.name));
    }

    public Map<String, List<String>> getProblemSetPathsByJids(Set<String> problemSetJids) {
        ImmutableMap.Builder<String, List<String>> pathsMap = ImmutableMap.builder();
        for (ProblemSetModel m : problemSetDao.selectByJids(problemSetJids).values()) {
            pathsMap.put(m.jid, ImmutableList.of(m.slug));
        }
        return pathsMap.build();
    }

    public Optional<List<String>> getProblemSetPathByJid(String problemSetJid) {
        Map<String, List<String>> pathsMap = getProblemSetPathsByJids(ImmutableSet.of(problemSetJid));
        return Optional.ofNullable(pathsMap.get(problemSetJid));
    }

    public ProblemSet createProblemSet(ProblemSetCreateData data) {
        if (problemSetDao.selectBySlug(data.getSlug()).isPresent()) {
            throw ProblemSetErrors.slugAlreadyExists(data.getSlug());
        }

        Optional<ArchiveModel> archiveModel = archiveDao.selectBySlug(data.getArchiveSlug());
        if (!archiveModel.isPresent()) {
            throw ProblemSetErrors.archiveSlugNotFound(data.getArchiveSlug());
        }

        ProblemSetModel model = new ProblemSetModel();
        model.archiveJid = archiveModel.get().jid;
        model.slug = data.getSlug();
        model.name = data.getName();
        model.description = data.getDescription().orElse("");
        model.contestTime = data.getContestTime().orElse(Instant.ofEpochMilli(0));
        return fromModel(problemSetDao.insert(model));
    }

    public Optional<ProblemSet> updateProblemSet(String problemSetJid, ProblemSetUpdateData data) {
        return problemSetDao.selectByJid(problemSetJid).map(model -> {
            if (data.getSlug().isPresent()) {
                String newSlug = data.getSlug().get();
                if (model.slug == null || !model.slug.equals(newSlug)) {
                    if (problemSetDao.selectBySlug(newSlug).isPresent()) {
                        throw ProblemSetErrors.slugAlreadyExists(newSlug);
                    }
                }
            }

            if (data.getArchiveSlug().isPresent()) {
                String newArchiveSlug = data.getArchiveSlug().get();
                Optional<ArchiveModel> archiveModel = archiveDao.selectBySlug(newArchiveSlug);
                if (!archiveModel.isPresent()) {
                    throw ProblemSetErrors.archiveSlugNotFound(newArchiveSlug);
                }
                model.archiveJid = archiveModel.get().jid;
            }


            data.getSlug().ifPresent(slug -> model.slug = slug);
            data.getName().ifPresent(name -> model.name = name);
            data.getDescription().ifPresent(description -> model.description = description);
            data.getContestTime().ifPresent(contestTime -> model.contestTime = contestTime);
            return fromModel(problemSetDao.update(model));
        });
    }

    private static ProblemSet fromModel(ProblemSetModel model) {
        return new ProblemSet.Builder()
                .id(model.id)
                .jid(model.jid)
                .archiveJid(model.archiveJid)
                .slug(Optional.ofNullable(model.slug).orElse("" + model.id))
                .name(model.name)
                .description(model.description)
                .contestTime(model.contestTime)
                .build();
    }
}
