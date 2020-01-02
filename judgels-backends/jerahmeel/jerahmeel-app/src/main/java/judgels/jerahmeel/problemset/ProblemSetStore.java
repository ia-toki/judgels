package judgels.jerahmeel.problemset;

import static judgels.jerahmeel.JerahmeelCacheUtils.getShortDuration;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.jerahmeel.api.problemset.ProblemSet;
import judgels.jerahmeel.api.problemset.ProblemSetCreateData;
import judgels.jerahmeel.persistence.ProblemSetDao;
import judgels.jerahmeel.persistence.ProblemSetModel;
import judgels.persistence.SearchOptions;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;

public class ProblemSetStore {
    private final ProblemSetDao problemSetDao;

    private final LoadingCache<String, ProblemSet> problemSetByJidCache;
    private final LoadingCache<String, ProblemSet> problemSetBySlugCache;

    @Inject
    public ProblemSetStore(ProblemSetDao problemSetDao) {
        this.problemSetDao = problemSetDao;

        this.problemSetByJidCache = Caffeine.newBuilder()
                .maximumSize(100)
                .expireAfterWrite(getShortDuration())
                .build(this::getProblemSetByJidUncached);
        this.problemSetBySlugCache = Caffeine.newBuilder()
                .maximumSize(100)
                .expireAfterWrite(getShortDuration())
                .build(this::getProblemSetBySlugUncached);
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

    private ProblemSet getProblemSetBySlugUncached(String problemSetSlug) {
        return problemSetDao.selectBySlug(problemSetSlug).map(ProblemSetStore::fromModel).orElse(null);
    }

    public Page<ProblemSet> getProblemSets(Optional<String> archiveJid, Optional<String> name, Optional<Integer> page) {
        SearchOptions.Builder searchOptions = new SearchOptions.Builder();
        name.ifPresent(e -> searchOptions.putTerms("name", e));

        SelectionOptions.Builder selectionOptions = new SelectionOptions.Builder().from(SelectionOptions.DEFAULT_PAGED);
        page.ifPresent(selectionOptions::page);
        if (!name.orElse("").isEmpty() || archiveJid.isPresent()) {
            selectionOptions.orderBy("name");
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
        return problemSetDao.selectByJids(problemSetJids)
                .values()
                .stream()
                .collect(Collectors.toMap(
                        c -> c.jid,
                        c -> ImmutableList.of(Optional.ofNullable(c.slug).orElse("" + c.id))));
    }

    public ProblemSet createProblemSet(ProblemSetCreateData data) {
        ProblemSetModel model = new ProblemSetModel();
        model.archiveJid = "";
        model.slug = data.getSlug();
        model.name = data.getSlug();
        model.description = "";
        return fromModel(problemSetDao.insert(model));
    }

    private static ProblemSet fromModel(ProblemSetModel model) {
        return new ProblemSet.Builder()
                .id(model.id)
                .jid(model.jid)
                .archiveJid(model.archiveJid)
                .slug(Optional.ofNullable(model.slug).orElse("" + model.id))
                .name(model.name)
                .description(model.description)
                .build();
    }
}
