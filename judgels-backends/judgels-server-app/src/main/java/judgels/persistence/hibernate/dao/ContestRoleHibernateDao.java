package judgels.persistence.hibernate.dao;

import static judgels.api.contest.contestant.ContestContestantStatus.APPROVED;
import static judgels.persistence.CriteriaPredicate.and;
import static judgels.persistence.CriteriaPredicate.not;
import static judgels.persistence.CriteriaPredicate.or;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import judgels.api.contest.module.ContestModuleType;
import judgels.persistence.CriteriaPredicate;
import judgels.persistence.dao.ContestRoleDao;
import judgels.persistence.model.ContestContestantModel;
import judgels.persistence.model.ContestContestantModel_;
import judgels.persistence.model.ContestManagerModel;
import judgels.persistence.model.ContestManagerModel_;
import judgels.persistence.model.ContestModel;
import judgels.persistence.model.ContestModel_;
import judgels.persistence.model.ContestModuleModel;
import judgels.persistence.model.ContestModuleModel_;
import judgels.persistence.model.ContestSupervisorModel;
import judgels.persistence.model.ContestSupervisorModel_;

@Singleton
public class ContestRoleHibernateDao extends JudgelsHibernateDao<ContestModel> implements ContestRoleDao {
    @Inject
    public ContestRoleHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public boolean isViewerOrAbove(String userJid, String contestJid) {
        return select()
                .where(contestIs(contestJid))
                .where(userCanView(userJid))
                .unique()
                .isPresent();
    }

    @Override
    public boolean isContestant(String userJid, String contestJid) {
        return select()
                .where(contestIs(contestJid))
                .where(userCanViewAsContestant(userJid))
                .unique()
                .isPresent();
    }

    @Override
    public boolean isSupervisorOrAbove(String userJid, String contestJid) {
        return select()
                .where(contestIs(contestJid))
                .where(userCanViewAsSupervisorOrAbove(userJid))
                .unique()
                .isPresent();
    }

    @Override
    public boolean isManager(String userJid, String contestJid) {
        return select()
                .where(contestIs(contestJid))
                .where(userIsManager(userJid))
                .unique()
                .isPresent();
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
