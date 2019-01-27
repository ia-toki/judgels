package org.iatoki.judgels.jerahmeel.statistic.problem;

import judgels.persistence.ActorProvider;
import judgels.persistence.hibernate.HibernateDao;
import org.hibernate.SessionFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Clock;

@Singleton
public final class ProblemStatisticEntryHibernateDao extends HibernateDao<ProblemStatisticEntryModel> implements ProblemStatisticEntryDao {

    @Inject
    public ProblemStatisticEntryHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
    }
}
