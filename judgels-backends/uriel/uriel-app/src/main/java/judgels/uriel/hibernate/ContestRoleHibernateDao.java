package judgels.uriel.hibernate;

import static judgels.persistence.CustomPredicateFilter.or;
import static judgels.uriel.UrielCacheUtils.SEPARATOR;
import static judgels.uriel.UrielCacheUtils.getShortDuration;
import static judgels.uriel.hibernate.ContestContestantHibernateDao.hasContestant;
import static judgels.uriel.hibernate.ContestHibernateDao.hasContestJid;
import static judgels.uriel.hibernate.ContestManagerHibernateDao.hasManager;
import static judgels.uriel.hibernate.ContestModuleHibernateDao.hasModule;
import static judgels.uriel.hibernate.ContestSupervisorHibernateDao.hasSupervisor;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Clock;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.persistence.ActorProvider;
import judgels.persistence.CustomPredicateFilter;
import judgels.persistence.FilterOptions;
import judgels.persistence.hibernate.JudgelsHibernateDao;
import judgels.uriel.api.contest.module.ContestModuleType;
import judgels.uriel.persistence.ContestModel;
import judgels.uriel.persistence.ContestRoleDao;
import org.hibernate.SessionFactory;

@Singleton
public class ContestRoleHibernateDao extends JudgelsHibernateDao<ContestModel> implements ContestRoleDao {
    private final Cache<String, Boolean> viewerOrAboveCache;
    private final Cache<String, Boolean> contestantCache;
    private final Cache<String, Boolean> managerCache;

    @Inject
    public ContestRoleHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);

        this.viewerOrAboveCache = Caffeine.newBuilder()
                .maximumSize(1_000)
                .expireAfterWrite(getShortDuration())
                .build();
        this.contestantCache = Caffeine.newBuilder()
                .maximumSize(1_000)
                .expireAfterWrite(getShortDuration())
                .build();
        this.managerCache = Caffeine.newBuilder()
                .maximumSize(1_000)
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
                .addCustomPredicates(hasViewerOrAbove(userJid))
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
                .addCustomPredicates(hasContestant(userJid))
                .build()).isPresent();
    }

    @Override
    public boolean isManager(String userJid, String contestJid) {
        return managerCache.get(
                userJid + SEPARATOR + contestJid,
                $ -> isManagerUncached(userJid, contestJid));
    }

    @Override
    public void invalidateCaches(String userJid, String contestJid) {
        viewerOrAboveCache.invalidate(userJid + SEPARATOR + contestJid);
        contestantCache.invalidate(userJid + SEPARATOR + contestJid);
        managerCache.invalidate(userJid + SEPARATOR + contestJid);
    }

    private boolean isManagerUncached(String userJid, String contestJid) {
        return selectByFilter(new FilterOptions.Builder<ContestModel>()
                .addCustomPredicates(hasContestJid(contestJid))
                .addCustomPredicates(hasManager(userJid))
                .build()).isPresent();
    }

    static CustomPredicateFilter<ContestModel> hasViewerOrAbove(String userJid) {
        return or(
                hasModule(ContestModuleType.REGISTRATION),
                hasContestant(userJid),
                hasSupervisor(userJid),
                hasManager(userJid));
    }

    static CustomPredicateFilter<ContestModel> hasSupervisorOrAbove(String userJid) {
        return or(
                hasSupervisor(userJid),
                hasManager(userJid));
    }
}
