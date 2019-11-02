package org.iatoki.judgels.jerahmeel.submission.programming;

import judgels.jerahmeel.persistence.ProgrammingSubmissionModel;
import judgels.persistence.hibernate.HibernateDaoData;
import org.iatoki.judgels.sandalphon.problem.programming.submission.AbstractProgrammingSubmissionHibernateDao;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class ProgrammingSubmissionHibernateDao extends AbstractProgrammingSubmissionHibernateDao<ProgrammingSubmissionModel> implements ProgrammingSubmissionDao {

    @Inject
    public ProgrammingSubmissionHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public ProgrammingSubmissionModel createSubmissionModel() {
        return new ProgrammingSubmissionModel();
    }
}
