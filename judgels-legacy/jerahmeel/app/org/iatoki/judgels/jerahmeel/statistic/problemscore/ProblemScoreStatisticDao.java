package org.iatoki.judgels.jerahmeel.statistic.problemscore;

import com.google.inject.ImplementedBy;
import judgels.persistence.JudgelsDao;

@ImplementedBy(ProblemScoreStatisticHibernateDao.class)
public interface ProblemScoreStatisticDao extends JudgelsDao<ProblemScoreStatisticModel> {

}
