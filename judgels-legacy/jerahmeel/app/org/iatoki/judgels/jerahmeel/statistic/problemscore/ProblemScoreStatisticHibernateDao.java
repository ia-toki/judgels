package org.iatoki.judgels.jerahmeel.statistic.problemscore;

import judgels.persistence.ActorProvider;
import judgels.persistence.hibernate.JudgelsHibernateDao;
import org.hibernate.SessionFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Clock;

@Singleton
public final class ProblemScoreStatisticHibernateDao extends JudgelsHibernateDao<ProblemScoreStatisticModel> implements ProblemScoreStatisticDao {

    @Inject
    public ProblemScoreStatisticHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
    }
}
