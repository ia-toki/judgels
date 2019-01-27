package org.iatoki.judgels.sandalphon.problem.programming.grading;

import judgels.persistence.ActorProvider;
import org.hibernate.SessionFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Clock;

@Singleton
public final class ProgrammingGradingHibernateDao extends AbstractProgrammingGradingHibernateDao<ProgrammingGradingModel> implements ProgrammingGradingDao {

    @Inject
    public ProgrammingGradingHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
    }

    @Override
    public ProgrammingGradingModel createGradingModel() {
        return new ProgrammingGradingModel();
    }
}
