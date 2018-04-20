package judgels.uriel.hibernate;

import java.time.Clock;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import judgels.persistence.ActorProvider;
import judgels.persistence.CustomPredicateFilter;
import judgels.persistence.hibernate.HibernateDao;
import judgels.uriel.persistence.ContestModel;
import judgels.uriel.persistence.ContestModel_;
import judgels.uriel.persistence.ContestSupervisorDao;
import judgels.uriel.persistence.ContestSupervisorModel;
import judgels.uriel.persistence.ContestSupervisorModel_;
import org.hibernate.SessionFactory;

@Singleton
public class ContestSupervisorHibernateDao extends HibernateDao<ContestSupervisorModel> implements
        ContestSupervisorDao {

    @Inject
    public ContestSupervisorHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
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
}
