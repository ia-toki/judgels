package org.iatoki.judgels.sandalphon.problem.programming.submission;

import judgels.persistence.hibernate.HibernateDaoData;

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
