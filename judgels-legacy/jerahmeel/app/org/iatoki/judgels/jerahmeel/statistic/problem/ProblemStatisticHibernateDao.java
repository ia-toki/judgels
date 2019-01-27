package org.iatoki.judgels.jerahmeel.statistic.problem;

import judgels.persistence.ActorProvider;
import judgels.persistence.hibernate.JudgelsHibernateDao;
import org.hibernate.SessionFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Clock;

@Singleton
public final class ProblemStatisticHibernateDao extends JudgelsHibernateDao<ProblemStatisticModel> implements ProblemStatisticDao {

    @Inject
    public ProblemStatisticHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
    }
}
