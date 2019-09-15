package judgels.uriel.hibernate;

import static judgels.persistence.CustomPredicateFilter.and;
import static judgels.persistence.CustomPredicateFilter.not;
import static judgels.persistence.CustomPredicateFilter.or;
import static judgels.uriel.UrielCacheUtils.SEPARATOR;
import static judgels.uriel.UrielCacheUtils.getShortDuration;
import static judgels.uriel.hibernate.ContestContestantHibernateDao.hasContestant;
import static judgels.uriel.hibernate.ContestContestantHibernateDao.hasParticipatingContestant;
import static judgels.uriel.hibernate.ContestHibernateDao.hasContestJid;
import static judgels.uriel.hibernate.ContestManagerHibernateDao.hasManager;
import static judgels.uriel.hibernate.ContestModuleHibernateDao.hasModule;
import static judgels.uriel.hibernate.ContestSupervisorHibernateDao.hasSupervisor;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.persistence.CustomPredicateFilter;
import judgels.persistence.FilterOptions;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.JudgelsHibernateDao;
import judgels.uriel.api.contest.module.ContestModuleType;
import judgels.uriel.persistence.ContestModel;
import judgels.uriel.persistence.ContestRoleDao;

@Singleton
public class ContestRoleHibernateDao extends JudgelsHibernateDao<ContestModel> implements ContestRoleDao {
    private final Cache<String, Boolean> viewerOrAboveCache;
    private final Cache<String, Boolean> contestantCache;
    private final Cache<String, Boolean> supervisorOrAboveCache;
    private final Cache<String, Boolean> managerCache;

    @Inject
    public ContestRoleHibernateDao(HibernateDaoData data) {
        super(data);

        this.viewerOrAboveCache = Caffeine.newBuilder()
                .maximumSize(1_000)
                .expireAfterWrite(getShortDuration())
                .build();
        this.contestantCache = Caffeine.newBuilder()
                .maximumSize(1_000)
                .expireAfterWrite(getShortDuration())
                .build();
        this.supervisorOrAboveCache = Caffeine.newBuilder()
                .maximumSize(100)
                .expireAfterWrite(getShortDuration())
                .build();
        this.managerCache = Caffeine.newBuilder()
                .maximumSize(100)
                .expireAfterWrite(getShortDuration())
                .build();
    }

    @Override
    public boolean isViewerOrAbove(String userJid, String contestJid) {
        return viewerOrAboveCache.get(
                userJid + SEPARATOR + contestJid,
                $ -> isViewerOrAboveUncached(userJid, contestJid));
    }

    private boolean isViewerOrAboveUncached(String userJid, String contestJid) {
        return selectByFilter(new FilterOptions.Builder<ContestModel>()
                .addCustomPredicates(hasContestJid(contestJid))
                .addCustomPredicates(isVisible(userJid))
                .build()).isPresent();
    }

    @Override
    public boolean isContestant(String userJid, String contestJid) {
        return contestantCache.get(
                userJid + SEPARATOR + contestJid,
                $ -> isContestantUncached(userJid, contestJid));
    }

    private boolean isContestantUncached(String userJid, String contestJid) {
        return selectByFilter(new FilterOptions.Builder<ContestModel>()
                .addCustomPredicates(hasContestJid(contestJid))
                .addCustomPredicates(isVisibleAsContestant(userJid))
                .build()).isPresent();
    }

    @Override
    public boolean isSupervisorOrAbove(String userJid, String contestJid) {
        return supervisorOrAboveCache.get(
                userJid + SEPARATOR + contestJid,
                $ -> isSupervisorOrAboveUncached(userJid, contestJid));
    }

    private boolean isSupervisorOrAboveUncached(String userJid, String contestJid) {
        return selectByFilter(new FilterOptions.Builder<ContestModel>()
                .addCustomPredicates(hasContestJid(contestJid))
                .addCustomPredicates(isVisibleAsSupervisorOrAbove(userJid))
                .build()).isPresent();
    }

    @Override
    public boolean isManager(String userJid, String contestJid) {
        return managerCache.get(
                userJid + SEPARATOR + contestJid,
                $ -> isManagerUncached(userJid, contestJid));
    }

    private boolean isManagerUncached(String userJid, String contestJid) {
        return selectByFilter(new FilterOptions.Builder<ContestModel>()
                .addCustomPredicates(hasContestJid(contestJid))
                .addCustomPredicates(hasManager(userJid))
                .build()).isPresent();
    }

    @Override
    public void invalidateCaches(String userJid, String contestJid) {
        viewerOrAboveCache.invalidate(userJid + SEPARATOR + contestJid);
        contestantCache.invalidate(userJid + SEPARATOR + contestJid);
        supervisorOrAboveCache.invalidate(userJid + SEPARATOR + contestJid);
        managerCache.invalidate(userJid + SEPARATOR + contestJid);
    }

    @Override
    public void invalidateCaches() {
        viewerOrAboveCache.invalidateAll();
        contestantCache.invalidateAll();
        supervisorOrAboveCache.invalidateAll();
        managerCache.invalidateAll();
    }

    static CustomPredicateFilter<ContestModel> isVisible(String userJid) {
        return or(
                isVisibleAsViewer(),
                isVisibleAsContestant(userJid),
                isVisibleAsSupervisorOrAbove(userJid));
    }

    static CustomPredicateFilter<ContestModel> isVisibleAsViewer() {
        return and(hasModule(ContestModuleType.REGISTRATION), not(hasModule(ContestModuleType.HIDDEN)));
    }

    static CustomPredicateFilter<ContestModel> isVisibleAsContestant(String userJid) {
        return and(hasContestant(userJid), not(hasModule(ContestModuleType.HIDDEN)));
    }

    static CustomPredicateFilter<ContestModel> isContestantParticipationVisibleAsViewer(String userJid) {
        return and(
                hasParticipatingContestant(userJid),
                hasModule(ContestModuleType.REGISTRATION),
                not(hasModule(ContestModuleType.HIDDEN)));
    }

    static CustomPredicateFilter<ContestModel> isVisibleAsSupervisor(String userJid) {
        return and(hasSupervisor(userJid), not(hasModule(ContestModuleType.HIDDEN)));
    }

    static CustomPredicateFilter<ContestModel> isVisibleAsSupervisorOrAbove(String userJid) {
        return or(
                isVisibleAsSupervisor(userJid),
                hasManager(userJid));
    }
}
