package org.iatoki.judgels.jerahmeel.statistic.point;

import judgels.persistence.ActorProvider;
import judgels.persistence.hibernate.JudgelsHibernateDao;
import org.hibernate.SessionFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Clock;

@Singleton
public final class PointStatisticHibernateDao extends JudgelsHibernateDao<PointStatisticModel> implements PointStatisticDao {

    @Inject
    public PointStatisticHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
    }
}
