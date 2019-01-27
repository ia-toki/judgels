package org.iatoki.judgels.jerahmeel.statistic.problemscore;

import com.google.inject.ImplementedBy;
import judgels.persistence.Dao;

@ImplementedBy(ProblemScoreStatisticEntryHibernateDao.class)
public interface ProblemScoreStatisticEntryDao extends Dao<ProblemScoreStatisticEntryModel> {

}
