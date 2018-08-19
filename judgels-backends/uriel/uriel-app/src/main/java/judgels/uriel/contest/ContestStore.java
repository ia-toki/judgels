package judgels.uriel.contest;

import static java.time.temporal.ChronoUnit.MILLIS;
import static judgels.uriel.UrielCacheUtils.getShortDuration;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.collect.Lists;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestData;
import judgels.uriel.api.contest.ContestDescription;
import judgels.uriel.api.contest.ContestStyle;
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

    public Page<Contest> getContests(String userJid, SelectionOptions options) {
        Page<ContestModel> models = adminRoleDao.isAdmin(userJid)
                ? contestDao.selectPaged(options)
                : contestDao.selectPagedByUserJid(userJid, options);
        return models.mapData(data -> Lists.transform(data, ContestStore::fromModel));
    }

    public List<Contest> getActiveContests(String userJid, SelectionOptions options) {
        List<ContestModel> models = adminRoleDao.isAdmin(userJid)
                ? contestDao.selectAllActive(options)
                : contestDao.selectAllActiveByUserJid(userJid, options);
        return Lists.transform(models, ContestStore::fromModel);
    }

    public Page<Contest> getPastContests(String userJid, SelectionOptions options) {
        Page<ContestModel> models = adminRoleDao.isAdmin(userJid)
                ? contestDao.selectPagedPast(options)
                : contestDao.selectPagedPastByUserJid(userJid, options);
        return models.mapData(data -> Lists.transform(data, ContestStore::fromModel));
    }

    public Contest createContest(ContestData contestData) {
        ContestModel model = new ContestModel();
        toModel(contestData, model);
        return fromModel(contestDao.insert(model));
    }

    public ContestDescription getContestDescription(Contest contest) {
        return new ContestDescription.Builder()
                .description(contest.getDescription())
                .build();
    }

    private static Contest fromModel(ContestModel model) {
        return new Contest.Builder()
                .id(model.id)
                .jid(model.jid)
                .slug(Optional.ofNullable(model.slug).orElse("" + model.id))
                .name(model.name)
                .description(model.description)
                .style(ContestStyle.valueOf(model.style))
                .beginTime(model.beginTime)
                .duration(Duration.of(model.duration, MILLIS))
                .build();
    }

    private static void toModel(ContestData data, ContestModel model) {
        model.name = data.getName();
        model.slug = data.getSlug().orElse(null);
        model.description = data.getDescription();
        model.style = data.getStyle().name();
        model.beginTime = data.getBeginTime();
        model.duration = data.getDuration().toMillis();
    }
}
