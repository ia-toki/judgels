package judgels.uriel.hibernate;

import static judgels.uriel.hibernate.ContestRoleHibernateDao.isContestantPredicate;
import static judgels.uriel.hibernate.ContestRoleHibernateDao.isManagerPredicate;
import static judgels.uriel.hibernate.ContestRoleHibernateDao.isSupervisorPredicate;

import java.time.Clock;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.persistence.ActorProvider;
import judgels.persistence.FilterOptions;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;
import judgels.persistence.hibernate.JudgelsHibernateDao;
import judgels.uriel.persistence.ContestDao;
import judgels.uriel.persistence.ContestModel;
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
                .addCustomPredicates((cb, cq, root) -> cb.or(
                        isContestantPredicate(cb, cq, root, userJid),
                        isSupervisorPredicate(cb, cq, root, userJid),
                        isManagerPredicate(cb, cq, root, userJid)))
                .build(), options);
    }
}
