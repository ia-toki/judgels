package org.iatoki.judgels.sandalphon.problem.programming.grading;

import com.google.inject.ImplementedBy;
import judgels.sandalphon.persistence.BaseProgrammingGradingDao;

@ImplementedBy(ProgrammingGradingHibernateDao.class)
public interface ProgrammingGradingDao extends BaseProgrammingGradingDao<ProgrammingGradingModel> {}
