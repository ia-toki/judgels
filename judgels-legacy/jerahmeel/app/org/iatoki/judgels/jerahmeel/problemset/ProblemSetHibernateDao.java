package org.iatoki.judgels.jerahmeel.problemset;

import judgels.persistence.ActorProvider;
import judgels.persistence.hibernate.JudgelsHibernateDao;
import org.hibernate.SessionFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Clock;

@Singleton
public final class ProblemSetHibernateDao extends JudgelsHibernateDao<ProblemSetModel> implements ProblemSetDao {

    @Inject
    public ProblemSetHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
    }
}
