package judgels.jerahmeel.problemset;

import static judgels.jerahmeel.JerahmeelCacheUtils.getShortDuration;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
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
import judgels.jerahmeel.persistence.ProblemSetModel;
import judgels.jerahmeel.persistence.ProblemSetProblemDao;
import judgels.jerahmeel.persistence.ProblemSetProblemModel;
import judgels.persistence.SearchOptions;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;

public class ProblemSetStore {
    private final ProblemSetDao problemSetDao;
    private final ProblemSetProblemDao problemSetProblemDao;
    private final ProblemContestDao problemContestDao;
    private final ArchiveDao archiveDao;

    private final LoadingCache<String, ProblemSet> problemSetByJidCache;
    private final LoadingCache<String, ProblemSet> problemSetBySlugCache;
    private final LoadingCache<String, ProblemSet> problemSetByContestJidCache;

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

        this.problemSetByJidCache = Caffeine.newBuilder()
                .maximumSize(100)
                .expireAfterWrite(getShortDuration())
                .build(this::getProblemSetByJidUncached);
        this.problemSetBySlugCache = Caffeine.newBuilder()
                .maximumSize(100)
                .expireAfterWrite(getShortDuration())
                .build(this::getProblemSetBySlugUncached);
        this.problemSetByContestJidCache = Caffeine.newBuilder()
                .maximumSize(100)
                .expireAfterWrite(getShortDuration())
                .build(this::getProblemSetByContestJidUncached);
    }

    public Optional<ProblemSet> getProblemSetByJid(String problemSetJid) {
        return Optional.ofNullable(problemSetByJidCache.get(problemSetJid));
    }

    private ProblemSet getProblemSetByJidUncached(String problemSetJid) {
        return problemSetDao.selectByJid(problemSetJid).map(ProblemSetStore::fromModel).orElse(null);
    }

    public Optional<ProblemSet> getProblemSetBySlug(String problemSetSlug) {
        return Optional.ofNullable(problemSetBySlugCache.get(problemSetSlug));
    }

    public Map<String, ProblemSet> getProblemSetsBySlugs(Set<String> problemSetSlugs) {
        return problemSetBySlugCache.getAll(problemSetSlugs);
    }

    private ProblemSet getProblemSetBySlugUncached(String problemSetSlug) {
        return problemSetDao.selectBySlug(problemSetSlug).map(ProblemSetStore::fromModel).orElse(null);
    }

    public Optional<ProblemSet> getProblemSetByContestJid(String contestJid) {
        return Optional.ofNullable(problemSetByContestJidCache.get(contestJid));
    }

    private ProblemSet getProblemSetByContestJidUncached(String contestJid) {
        for (ProblemContestModel pcm : problemContestDao.selectAllByContestJid(contestJid)) {
            for (ProblemSetProblemModel pspm : problemSetProblemDao.selectAllByProblemJid(pcm.problemJid)) {
                Optional<ProblemSetModel> m = problemSetDao.selectByJid(pspm.problemSetJid);
                if (m.isPresent()) {
                    return fromModel(m.get());
                }
            }
        }
        return null;
    }

    public Page<ProblemSet> getProblemSets(Optional<String> archiveJid, Optional<String> name, Optional<Integer> page) {
        SearchOptions.Builder searchOptions = new SearchOptions.Builder();
        name.ifPresent(e -> searchOptions.putTerms("name", e));

        SelectionOptions.Builder selectionOptions = new SelectionOptions.Builder().from(SelectionOptions.DEFAULT_PAGED);
        page.ifPresent(selectionOptions::page);

        if (!name.orElse("").isEmpty() || archiveJid.isPresent()) {
            selectionOptions.orderBy("contestTime");
            selectionOptions.orderBy2("name");
        }

        Page<ProblemSetModel> models =
                problemSetDao.selectPaged(archiveJid, searchOptions.build(), selectionOptions.build());
        return models.mapPage(p -> Lists.transform(p, ProblemSetStore::fromModel));
    }

    public Map<String, String> getProblemSetNamesByJids(Set<String> problemSetJids) {
        return problemSetDao.selectByJids(problemSetJids)
                .values()
                .stream()
                .collect(Collectors.toMap(
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

            problemSetByJidCache.invalidate(problemSetJid);
            if (model.slug != null) {
                problemSetBySlugCache.invalidate(model.slug);
            }
            if (data.getSlug().isPresent()) {
                problemSetBySlugCache.invalidate(data.getSlug().get());
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
