package org.iatoki.judgels.jerahmeel.submission.programming;

import com.google.inject.ImplementedBy;
import org.iatoki.judgels.sandalphon.problem.programming.submission.BaseProgrammingSubmissionDao;

@ImplementedBy(ProgrammingSubmissionHibernateDao.class)
public interface ProgrammingSubmissionDao extends BaseProgrammingSubmissionDao<ProgrammingSubmissionModel> {

}
