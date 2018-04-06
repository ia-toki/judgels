package judgels.uriel.hibernate;

import java.time.Clock;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import judgels.persistence.ActorProvider;
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
                .addCustomPredicates((cb, cq, root) -> cb.equal(root.get(ContestModel_.jid), contestJid))
                .addCustomPredicates((cb, cq, root) -> cb.or(
                        isContestantPredicate(cb, cq, root, userJid),
                        isSupervisorPredicate(cb, cq, root, userJid),
                        isManagerPredicate(cb, cq, root, userJid)))
                .build()).getData().isEmpty();
    }

    @Override
    public boolean isSupervisorOrAbove(String userJid, String contestJid) {
        return !selectAll(new FilterOptions.Builder<ContestModel>()
                .addCustomPredicates((cb, cq, root) -> cb.equal(root.get(ContestModel_.jid), contestJid))
                .addCustomPredicates((cb, cq, root) -> cb.or(
                        isSupervisorPredicate(cb, cq, root, userJid),
                        isManagerPredicate(cb, cq, root, userJid)))
                .build()).getData().isEmpty();
    }

    @Override
    public boolean isManager(String userJid, String contestJid) {
        return !selectAll(new FilterOptions.Builder<ContestModel>()
                .addCustomPredicates((cb, cq, root) -> cb.equal(root.get(ContestModel_.jid), contestJid))
                .addCustomPredicates((cb, cq, root) -> isManagerPredicate(cb, cq, root, userJid))
                .build()).getData().isEmpty();
    }

    static Predicate isContestantPredicate(
            CriteriaBuilder cb,
            CriteriaQuery<?> cq,
            Root<ContestModel> root,
            String userJid) {

        Subquery<ContestContestantModel> sq = cq.subquery(ContestContestantModel.class);
        Root<ContestContestantModel> subRoot = sq.from(ContestContestantModel.class);

        sq.where(
                cb.equal(subRoot.get(ContestContestantModel_.contestJid), root.get(ContestModel_.jid)),
                cb.equal(subRoot.get(ContestContestantModel_.userJid), userJid));
        sq.select(subRoot);

        return cb.exists(sq);
    }

    static Predicate isSupervisorPredicate(
            CriteriaBuilder cb,
            CriteriaQuery<?> cq,
            Root<ContestModel> root,
            String userJid) {

        Subquery<ContestSupervisorModel> sq = cq.subquery(ContestSupervisorModel.class);
        Root<ContestSupervisorModel> subRoot = sq.from(ContestSupervisorModel.class);

        sq.where(
                cb.equal(subRoot.get(ContestSupervisorModel_.contestJid), root.get(ContestModel_.jid)),
                cb.equal(subRoot.get(ContestSupervisorModel_.userJid), userJid));
        sq.select(subRoot);

        return cb.exists(sq);
    }

    static Predicate isManagerPredicate(
            CriteriaBuilder cb,
            CriteriaQuery<?> cq,
            Root<ContestModel> root,
            String userJid) {

        Subquery<ContestManagerModel> sq = cq.subquery(ContestManagerModel.class);
        Root<ContestManagerModel> subRoot = sq.from(ContestManagerModel.class);

        sq.where(
                cb.equal(subRoot.get(ContestManagerModel_.contestJid), root.get(ContestModel_.jid)),
                cb.equal(subRoot.get(ContestManagerModel_.userJid), userJid));
        sq.select(subRoot);

        return cb.exists(sq);
    }
}
