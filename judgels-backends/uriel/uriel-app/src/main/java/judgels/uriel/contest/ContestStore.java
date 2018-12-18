package judgels.uriel.contest;

import static java.time.temporal.ChronoUnit.MILLIS;
import static judgels.uriel.UrielCacheUtils.getShortDuration;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.collect.Lists;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.persistence.SearchOptions;
import judgels.persistence.api.OrderDir;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestCreateData;
import judgels.uriel.api.contest.ContestDescription;
import judgels.uriel.api.contest.ContestErrors;
import judgels.uriel.api.contest.ContestStyle;
import judgels.uriel.api.contest.ContestUpdateData;
import judgels.uriel.persistence.AdminRoleDao;
import judgels.uriel.persistence.ContestDao;
import judgels.uriel.persistence.ContestModel;

@Singleton
public class ContestStore {
    private final AdminRoleDao adminRoleDao;
    private final ContestDao contestDao;

    private final LoadingCache<String, Contest> contestByJidCache;
    private final LoadingCache<String, Contest> contestBySlugCache;

    @Inject
    public ContestStore(AdminRoleDao adminRoleDao, ContestDao contestDao) {
        this.adminRoleDao = adminRoleDao;
        this.contestDao = contestDao;

        this.contestByJidCache = Caffeine.newBuilder()
                .maximumSize(100)
                .expireAfterWrite(getShortDuration())
                .build(this::getContestByJidUncached);
        this.contestBySlugCache = Caffeine.newBuilder()
                .maximumSize(100)
                .expireAfterWrite(getShortDuration())
                .build(this::getContestBySlugUncached);
    }

    public Optional<Contest> getContestByJid(String contestJid) {
        return Optional.ofNullable(contestByJidCache.get(contestJid));
    }

    private Contest getContestByJidUncached(String contestJid) {
        return contestDao.selectByJid(contestJid).map(ContestStore::fromModel).orElse(null);
    }

    public Optional<Contest> getContestBySlug(String contestSlug) {
        return Optional.ofNullable(contestBySlugCache.get(contestSlug));
    }

    private Contest getContestBySlugUncached(String contestSlug) {
        return contestDao.selectBySlug(contestSlug).map(ContestStore::fromModel).orElse(null);
    }

    public Page<Contest> getContests(String userJid, Optional<String> name, Optional<Integer> page) {
        SearchOptions.Builder searchOptions = new SearchOptions.Builder();
        name.ifPresent(e -> searchOptions.putTerms("name", e));

        SelectionOptions.Builder selectionOptions = new SelectionOptions.Builder().from(SelectionOptions.DEFAULT_PAGED);
        selectionOptions.orderBy("beginTime");
        page.ifPresent(selectionOptions::page);

        Page<ContestModel> models = adminRoleDao.isAdmin(userJid)
                ? contestDao.selectPaged(searchOptions.build(), selectionOptions.build())
                : contestDao.selectPagedByUserJid(userJid, searchOptions.build(), selectionOptions.build());
        return models.mapPage(p -> Lists.transform(p, ContestStore::fromModel));
    }

    public List<Contest> getActiveContests(String userJid) {
        SelectionOptions options = new SelectionOptions.Builder()
                .from(SelectionOptions.DEFAULT_ALL)
                .orderBy("beginTime")
                .orderDir(OrderDir.ASC)
                .build();

        List<ContestModel> models = adminRoleDao.isAdmin(userJid)
                ? contestDao.selectAllActive(options)
                : contestDao.selectAllActiveByUserJid(userJid, options);
        return Lists.transform(models, ContestStore::fromModel);
    }

    public Contest createContest(ContestCreateData contestCreateData) {
        if (contestDao.selectBySlug(contestCreateData.getSlug()).isPresent()) {
            throw ContestErrors.contestSlugAlreadyExists(contestCreateData.getSlug());
        }

        ContestModel model = new ContestModel();
        model.slug = contestCreateData.getSlug();
        model.name = contestCreateData.getSlug();
        model.description = "";
        model.style = ContestStyle.ICPC.name();
        model.beginTime = Instant.ofEpochSecond(4102444800L); // 1 January 2100
        model.duration = Duration.ofHours(5).toMillis();

        return fromModel(contestDao.insert(model));
    }

    public Optional<Contest> updateContest(String contestJid, ContestUpdateData contestUpdateData) {
        return contestDao.selectByJid(contestJid).map(model -> {
            if (contestUpdateData.getSlug().isPresent()) {
                String newSlug = contestUpdateData.getSlug().get();
                if (model.slug == null || !model.slug.equals(newSlug)) {
                    if (contestDao.selectBySlug(newSlug).isPresent()) {
                        throw ContestErrors.contestSlugAlreadyExists(newSlug);
                    }
                }
            }

            contestByJidCache.invalidate(contestJid);
            if (model.slug != null) {
                contestBySlugCache.invalidate(model.slug);
            }
            if (contestUpdateData.getSlug().isPresent()) {
                contestBySlugCache.invalidate(contestUpdateData.getSlug().get());
            }

            contestUpdateData.getSlug().ifPresent(slug -> model.slug = slug);
            contestUpdateData.getName().ifPresent(name -> model.name = name);
            contestUpdateData.getStyle().ifPresent(style -> model.style = style.name());
            contestUpdateData.getBeginTime().ifPresent(time -> model.beginTime = time);
            contestUpdateData.getDuration().ifPresent(duration -> model.duration = duration.toMillis());
            return fromModel(contestDao.update(model));
        });
    }

    public Optional<ContestDescription> getContestDescription(String contestJid) {
        return contestDao.selectByJid(contestJid).map(
                model -> new ContestDescription.Builder().description(model.description).build());
    }

    public Optional<ContestDescription> updateContestDescription(String contestJid, ContestDescription description) {
        return contestDao.selectByJid(contestJid).map(model -> {
            model.description = description.getDescription();
            contestDao.update(model);
            return description;
        });
    }

    private static Contest fromModel(ContestModel model) {
        return new Contest.Builder()
                .id(model.id)
                .jid(model.jid)
                .slug(Optional.ofNullable(model.slug).orElse("" + model.id))
                .name(model.name)
                .style(ContestStyle.valueOf(model.style))
                .beginTime(model.beginTime)
                .duration(Duration.of(model.duration, MILLIS))
                .build();
    }
}
