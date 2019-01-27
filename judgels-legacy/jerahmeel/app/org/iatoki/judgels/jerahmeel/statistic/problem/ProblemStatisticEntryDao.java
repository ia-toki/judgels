package org.iatoki.judgels.jerahmeel.statistic.problem;

import com.google.inject.ImplementedBy;
import judgels.persistence.Dao;

@ImplementedBy(ProblemStatisticEntryHibernateDao.class)
public interface ProblemStatisticEntryDao extends Dao<ProblemStatisticEntryModel> {

}
