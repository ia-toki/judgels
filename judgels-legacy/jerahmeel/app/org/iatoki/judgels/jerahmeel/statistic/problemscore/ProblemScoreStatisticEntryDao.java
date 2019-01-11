package org.iatoki.judgels.jerahmeel.statistic.problemscore;

import com.google.inject.ImplementedBy;
import org.iatoki.judgels.play.model.Dao;

@ImplementedBy(ProblemScoreStatisticEntryHibernateDao.class)
public interface ProblemScoreStatisticEntryDao extends Dao<Long, ProblemScoreStatisticEntryModel> {

}
