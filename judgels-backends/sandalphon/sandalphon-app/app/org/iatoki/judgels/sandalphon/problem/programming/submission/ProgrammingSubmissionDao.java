package org.iatoki.judgels.sandalphon.problem.programming.submission;

import com.google.inject.ImplementedBy;
import judgels.sandalphon.persistence.BaseProgrammingSubmissionDao;

@ImplementedBy(ProgrammingSubmissionHibernateDao.class)
public interface ProgrammingSubmissionDao extends BaseProgrammingSubmissionDao<ProgrammingSubmissionModel> {}
