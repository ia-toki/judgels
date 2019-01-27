package org.iatoki.judgels.jerahmeel.statistic.point;

import judgels.persistence.ActorProvider;
import judgels.persistence.hibernate.HibernateDao;
import org.hibernate.SessionFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Clock;

@Singleton
public final class PointStatisticEntryHibernateDao extends HibernateDao<PointStatisticEntryModel> implements PointStatisticEntryDao {

    @Inject
    public PointStatisticEntryHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
    }
}
