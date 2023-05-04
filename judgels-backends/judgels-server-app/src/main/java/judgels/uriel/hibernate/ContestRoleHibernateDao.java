package judgels.uriel.hibernate;

import static judgels.persistence.CriteriaPredicate.and;
import static judgels.persistence.CriteriaPredicate.not;
import static judgels.persistence.CriteriaPredicate.or;
import static judgels.uriel.UrielCacheUtils.SEPARATOR;
import static judgels.uriel.UrielCacheUtils.getShortDuration;
import static judgels.uriel.api.contest.contestant.ContestContestantStatus.APPROVED;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import judgels.persistence.CriteriaPredicate;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.JudgelsHibernateDao;
import judgels.uriel.api.contest.module.ContestModuleType;
import judgels.uriel.persistence.ContestContestantModel;
import judgels.uriel.persistence.ContestContestantModel_;
import judgels.uriel.persistence.ContestManagerModel;
import judgels.uriel.persistence.ContestManagerModel_;
import judgels.uriel.persistence.ContestModel;
import judgels.uriel.persistence.ContestModel_;
import judgels.uriel.persistence.ContestModuleModel;
import judgels.uriel.persistence.ContestModuleModel_;
import judgels.uriel.persistence.ContestRoleDao;
import judgels.uriel.persistence.ContestSupervisorModel;
import judgels.uriel.persistence.ContestSupervisorModel_;

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
        return select()
                .where(contestIs(contestJid))
                .where(userCanView(userJid))
                .unique()
                .isPresent();
    }

    @Override
    public boolean isContestant(String userJid, String contestJid) {
        return contestantCache.get(
                userJid + SEPARATOR + contestJid,
                $ -> isContestantUncached(userJid, contestJid));
    }

    private boolean isContestantUncached(String userJid, String contestJid) {
        return select()
                .where(contestIs(contestJid))
                .where(userCanViewAsContestant(userJid))
                .unique()
                .isPresent();
    }

    @Override
    public boolean isSupervisorOrAbove(String userJid, String contestJid) {
        return supervisorOrAboveCache.get(
                userJid + SEPARATOR + contestJid,
                $ -> isSupervisorOrAboveUncached(userJid, contestJid));
    }

    private boolean isSupervisorOrAboveUncached(String userJid, String contestJid) {
        return select()
                .where(contestIs(contestJid))
                .where(userCanViewAsSupervisorOrAbove(userJid))
                .unique()
                .isPresent();
    }

    @Override
    public boolean isManager(String userJid, String contestJid) {
        return managerCache.get(
                userJid + SEPARATOR + contestJid,
                $ -> isManagerUncached(userJid, contestJid));
    }

    private boolean isManagerUncached(String userJid, String contestJid) {
        return select()
                .where(contestIs(contestJid))
                .where(userIsManager(userJid))
                .unique()
                .isPresent();
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

    static CriteriaPredicate<ContestModel> contestIs(String contestJid) {
        return (cb, cq, root) -> cb.equal(root.get(ContestModel_.jid), contestJid);
    }

    static CriteriaPredicate<ContestModel> userCanView(String userJid) {
        return or(
                isPublic(),
                userCanViewAsContestant(userJid),
                userCanViewAsSupervisorOrAbove(userJid));
    }

    static CriteriaPredicate<ContestModel> isPublic() {
        return and(
                contestHasModule(ContestModuleType.REGISTRATION),
                not(contestHasModule(ContestModuleType.HIDDEN)));
    }

    static CriteriaPredicate<ContestModel> userCanViewAsContestant(String userJid) {
        return and(
                userIsContestant(userJid),
                not(contestHasModule(ContestModuleType.HIDDEN)));
    }

    static CriteriaPredicate<ContestModel> isVisibleAsSupervisor(String userJid) {
        return and(
                userIsSupervisor(userJid),
                not(contestHasModule(ContestModuleType.HIDDEN)));
    }

    static CriteriaPredicate<ContestModel> userCanViewAsSupervisorOrAbove(String userJid) {
        return or(
                isVisibleAsSupervisor(userJid),
                userIsManager(userJid));
    }

    static CriteriaPredicate<ContestModel> userIsContestant(String userJid) {
        return (cb, cq, root) -> {
            Subquery<ContestContestantModel> subquery = cq.subquery(ContestContestantModel.class);
            Root<ContestContestantModel> subroot = subquery.from(ContestContestantModel.class);

            return cb.exists(subquery
                    .select(subroot)
                    .where(
                            cb.equal(subroot.get(ContestContestantModel_.contestJid), root.get(ContestModel_.jid)),
                            cb.equal(subroot.get(ContestContestantModel_.userJid), userJid),
                            cb.equal(subroot.get(ContestContestantModel_.status), APPROVED.name())));
        };
    }

    static CriteriaPredicate<ContestModel> userParticipated(String userJid) {
        return (cb, cq, root) -> {
            Subquery<ContestContestantModel> subquery = cq.subquery(ContestContestantModel.class);
            Root<ContestContestantModel> subroot = subquery.from(ContestContestantModel.class);

            return cb.exists(subquery
                    .select(subroot)
                    .where(
                            cb.equal(subroot.get(ContestContestantModel_.contestJid), root.get(ContestModel_.jid)),
                            cb.equal(subroot.get(ContestContestantModel_.userJid), userJid),
                            cb.equal(subroot.get(ContestContestantModel_.status), APPROVED.name()),
                            cb.isNotNull(subroot.get(ContestContestantModel_.finalRank))));
        };
    }

    static CriteriaPredicate<ContestModel> userIsSupervisor(String userJid) {
        return (cb, cq, root) -> {
            Subquery<ContestSupervisorModel> subquery = cq.subquery(ContestSupervisorModel.class);
            Root<ContestSupervisorModel> subroot = subquery.from(ContestSupervisorModel.class);

            return cb.exists(subquery
                    .select(subroot)
                    .where(
                            cb.equal(subroot.get(ContestSupervisorModel_.contestJid), root.get(ContestModel_.jid)),
                            cb.equal(subroot.get(ContestSupervisorModel_.userJid), userJid)));
        };
    }

    static CriteriaPredicate<ContestModel> userIsManager(String userJid) {
        return (cb, cq, root) -> {
            Subquery<ContestManagerModel> subquery = cq.subquery(ContestManagerModel.class);
            Root<ContestManagerModel> subroot = subquery.from(ContestManagerModel.class);

            return cb.exists(subquery
                    .select(subroot)
                    .where(
                            cb.equal(subroot.get(ContestManagerModel_.contestJid), root.get(ContestModel_.jid)),
                            cb.equal(subroot.get(ContestManagerModel_.userJid), userJid)));
        };
    }

    static CriteriaPredicate<ContestModel> contestHasModule(ContestModuleType type) {
        return (cb, cq, root) -> {
            Subquery<ContestModuleModel> subquery = cq.subquery(ContestModuleModel.class);
            Root<ContestModuleModel> subroot = subquery.from(ContestModuleModel.class);

            return cb.exists(subquery
                    .select(subroot)
                    .where(
                            cb.equal(subroot.get(ContestModuleModel_.contestJid), root.get(ContestModel_.jid)),
                            cb.equal(subroot.get(ContestModuleModel_.name), type.name()),
                            cb.isTrue(subroot.get(ContestModuleModel_.enabled))));
        };
    }
}
