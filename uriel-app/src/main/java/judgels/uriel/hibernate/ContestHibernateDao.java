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
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;
import judgels.persistence.hibernate.JudgelsHibernateDao;
import judgels.uriel.persistence.ContestContestantModel;
import judgels.uriel.persistence.ContestContestantModel_;
import judgels.uriel.persistence.ContestDao;
import judgels.uriel.persistence.ContestModel;
import judgels.uriel.persistence.ContestModel_;
import org.hibernate.SessionFactory;

@Singleton
public class ContestHibernateDao extends JudgelsHibernateDao<ContestModel> implements ContestDao {
    @Inject
    public ContestHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
    }

    @Override
    public Page<ContestModel> selectAllByUserJid(String userJid, SelectionOptions options) {
        return selectAll(new FilterOptions.Builder<ContestModel>()
                .addCustomPredicates((cb, cq, root) -> isContestant(cb, cq, root, userJid))
                .build(), options);
    }

    private Predicate isContestant(
            CriteriaBuilder cb,
            CriteriaQuery<?> cq,
            Root<ContestModel> root,
            String userJid) {

        Subquery<ContestContestantModel> sq = cq.subquery(ContestContestantModel.class);
        Root<ContestContestantModel> subRoot = sq.from(ContestContestantModel.class);

        sq.where(cb.and(
                cb.equal(subRoot.get(ContestContestantModel_.contestJid), root.get(ContestModel_.jid)),
                cb.equal(subRoot.get(ContestContestantModel_.userJid), userJid)));
        sq.select(subRoot);

        return cb.exists(sq);
    }
}
