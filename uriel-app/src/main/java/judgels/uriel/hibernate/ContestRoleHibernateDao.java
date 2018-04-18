package judgels.uriel.hibernate;

import static judgels.persistence.CustomPredicateFilter.or;
import static judgels.uriel.hibernate.ContestHibernateDao.hasContestJid;

import java.time.Clock;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import judgels.persistence.ActorProvider;
import judgels.persistence.CustomPredicateFilter;
import judgels.persistence.FilterOptions;
import judgels.persistence.hibernate.JudgelsHibernateDao;
import judgels.uriel.persistence.ContestContestantModel;
import judgels.uriel.persistence.ContestContestantModel_;
import judgels.uriel.persistence.ContestManagerModel;
import judgels.uriel.persistence.ContestManagerModel_;
import judgels.uriel.persistence.ContestModel;
import judgels.uriel.persistence.ContestModel_;
import judgels.uriel.persistence.ContestRoleDao;
import judgels.uriel.persistence.ContestSupervisorModel;
import judgels.uriel.persistence.ContestSupervisorModel_;
import org.hibernate.SessionFactory;

@Singleton
public class ContestRoleHibernateDao extends JudgelsHibernateDao<ContestModel> implements ContestRoleDao {
    @Inject
    public ContestRoleHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
    }

    @Override
    public boolean isContestantOrAbove(String userJid, String contestJid) {
        return !selectAll(new FilterOptions.Builder<ContestModel>()
                .addCustomPredicates(hasContestJid(contestJid))
                .addCustomPredicates(or(
                        hasContestant(userJid),
                        hasSupervisor(userJid),
                        hasManager(userJid)))
                .build()).getData().isEmpty();
    }

    @Override
    public boolean isSupervisorOrAbove(String userJid, String contestJid) {
        return !selectAll(new FilterOptions.Builder<ContestModel>()
                .addCustomPredicates(hasContestJid(contestJid))
                .addCustomPredicates(or(
                        hasSupervisor(userJid),
                        hasManager(userJid)))
                .build()).getData().isEmpty();
    }

    @Override
    public boolean isManager(String userJid, String contestJid) {
        return !selectAll(new FilterOptions.Builder<ContestModel>()
                .addCustomPredicates(hasContestJid(contestJid))
                .addCustomPredicates(hasManager(userJid))
                .build()).getData().isEmpty();
    }

    static CustomPredicateFilter<ContestModel> hasContestant(String userJid) {
        return (cb, cq, root) -> {
            Subquery<ContestContestantModel> sq = cq.subquery(ContestContestantModel.class);
            Root<ContestContestantModel> subRoot = sq.from(ContestContestantModel.class);

            sq.where(
                    cb.equal(subRoot.get(ContestContestantModel_.contestJid), root.get(ContestModel_.jid)),
                    cb.equal(subRoot.get(ContestContestantModel_.userJid), userJid));
            sq.select(subRoot);

            return cb.exists(sq);
        };
    }

    static CustomPredicateFilter<ContestModel> hasSupervisor(String userJid) {
        return (cb, cq, root) -> {
            Subquery<ContestSupervisorModel> sq = cq.subquery(ContestSupervisorModel.class);
            Root<ContestSupervisorModel> subRoot = sq.from(ContestSupervisorModel.class);

            sq.where(
                    cb.equal(subRoot.get(ContestSupervisorModel_.contestJid), root.get(ContestModel_.jid)),
                    cb.equal(subRoot.get(ContestSupervisorModel_.userJid), userJid));
            sq.select(subRoot);

            return cb.exists(sq);
        };
    }

    static CustomPredicateFilter<ContestModel> hasManager(String userJid) {
        return (cb, cq, root) -> {
            Subquery<ContestManagerModel> sq = cq.subquery(ContestManagerModel.class);
            Root<ContestManagerModel> subRoot = sq.from(ContestManagerModel.class);

            sq.where(
                    cb.equal(subRoot.get(ContestManagerModel_.contestJid), root.get(ContestModel_.jid)),
                    cb.equal(subRoot.get(ContestManagerModel_.userJid), userJid));
            sq.select(subRoot);

            return cb.exists(sq);
        };
    }
}
