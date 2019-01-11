package org.iatoki.judgels.jerahmeel.submission.programming;

import org.iatoki.judgels.sandalphon.problem.programming.submission.AbstractProgrammingSubmissionHibernateDao;

import javax.inject.Singleton;

@Singleton
public final class ProgrammingSubmissionHibernateDao extends AbstractProgrammingSubmissionHibernateDao<ProgrammingSubmissionModel> implements ProgrammingSubmissionDao {

    public ProgrammingSubmissionHibernateDao() {
        super(ProgrammingSubmissionModel.class);
    }

    @Override
    public ProgrammingSubmissionModel createSubmissionModel() {
        return new ProgrammingSubmissionModel();
    }
}
