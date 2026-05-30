package tlx.problemset;

import static java.util.stream.Collectors.toMap;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import jakarta.inject.Inject;
import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import judgels.persistence.api.OrderDir;
import judgels.persistence.api.Page;
import judgels.persistence.dao.ArchiveDao;
import judgels.persistence.dao.ProblemContestDao;
import judgels.persistence.dao.ProblemSetDao;
import judgels.persistence.dao.ProblemSetDao.ProblemSetQueryBuilder;
import judgels.persistence.dao.ProblemSetProblemDao;
import judgels.persistence.model.ArchiveModel;
import judgels.persistence.model.ProblemContestModel;
import judgels.persistence.model.ProblemSetModel;
import judgels.persistence.model.ProblemSetModel_;
import judgels.persistence.model.ProblemSetProblemModel;
import tlx.api.problemset.ProblemSet;
import tlx.api.problemset.ProblemSetCreateData;
import tlx.api.problemset.ProblemSetErrors;
import tlx.api.problemset.ProblemSetUpdateData;

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

    public Map<String, ProblemSet> getProblemSetsBySlugs(Collection<String> problemSetSlugs) {
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

    public Map<String, String> getProblemSetNamesByJids(Collection<String> problemSetJids) {
        return problemSetDao.selectByJids(problemSetJids)
                .values()
                .stream()
                .collect(toMap(
                        c -> c.jid,
                        c -> c.name));
    }

    public Map<String, List<String>> getProblemSetPathsByJids(Collection<String> problemSetJids) {
        Map<String, List<String>> pathsMap = new HashMap<>();
        for (ProblemSetModel m : problemSetDao.selectByJids(problemSetJids).values()) {
            pathsMap.put(m.jid, ImmutableList.of(m.slug));
        }
        return Map.copyOf(pathsMap);
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

    public ProblemSet updateProblemSet(String problemSetJid, ProblemSetUpdateData data) {
        ProblemSetModel model = problemSetDao.findByJid(problemSetJid);
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
