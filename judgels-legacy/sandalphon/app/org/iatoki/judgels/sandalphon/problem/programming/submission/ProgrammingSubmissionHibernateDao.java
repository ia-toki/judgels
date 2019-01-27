package org.iatoki.judgels.sandalphon.problem.programming.submission;

import judgels.persistence.ActorProvider;
import org.hibernate.SessionFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Clock;

@Singleton
public final class ProgrammingSubmissionHibernateDao extends AbstractProgrammingSubmissionHibernateDao<ProgrammingSubmissionModel> implements ProgrammingSubmissionDao {

    @Inject
    public ProgrammingSubmissionHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
    }

    @Override
    public ProgrammingSubmissionModel createSubmissionModel() {
        return new ProgrammingSubmissionModel();
    }
}
