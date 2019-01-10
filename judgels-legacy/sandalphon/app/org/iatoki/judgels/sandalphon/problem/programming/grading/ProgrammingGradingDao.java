package org.iatoki.judgels.sandalphon.problem.programming.grading;

import com.google.inject.ImplementedBy;

@ImplementedBy(ProgrammingGradingHibernateDao.class)
public interface ProgrammingGradingDao extends BaseProgrammingGradingDao<ProgrammingGradingModel> {

}
