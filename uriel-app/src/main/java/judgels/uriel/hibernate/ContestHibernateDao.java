package judgels.uriel.hibernate;

import java.time.Clock;
import judgels.persistence.ActorProvider;
import judgels.persistence.hibernate.JudgelsHibernateDao;
import judgels.uriel.contest.ContestDao;
import judgels.uriel.contest.ContestModel;
import org.hibernate.SessionFactory;

public class ContestHibernateDao extends JudgelsHibernateDao<ContestModel> implements ContestDao {
    public ContestHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
    }
}
