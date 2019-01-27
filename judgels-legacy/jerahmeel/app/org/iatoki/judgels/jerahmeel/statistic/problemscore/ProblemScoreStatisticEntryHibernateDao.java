package org.iatoki.judgels.jerahmeel.statistic.problemscore;

import judgels.persistence.ActorProvider;
import judgels.persistence.hibernate.HibernateDao;
import org.hibernate.SessionFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Clock;

@Singleton
public final class ProblemScoreStatisticEntryHibernateDao extends HibernateDao<ProblemScoreStatisticEntryModel> implements ProblemScoreStatisticEntryDao {

    @Inject
    public ProblemScoreStatisticEntryHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
    }
}
