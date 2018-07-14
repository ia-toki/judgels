package judgels.uriel.hibernate;

import java.time.Clock;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import judgels.persistence.ActorProvider;
import judgels.persistence.CustomPredicateFilter;
import judgels.persistence.hibernate.HibernateDao;
import judgels.uriel.persistence.ContestManagerDao;
import judgels.uriel.persistence.ContestManagerModel;
import judgels.uriel.persistence.ContestManagerModel_;
import judgels.uriel.persistence.ContestModel;
import judgels.uriel.persistence.ContestModel_;
import org.hibernate.SessionFactory;

@Singleton
public class ContestManagerHibernateDao extends HibernateDao<ContestManagerModel> implements ContestManagerDao {
    @Inject
    public ContestManagerHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
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
