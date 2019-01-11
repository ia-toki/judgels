package org.iatoki.judgels.jerahmeel.statistic.problemscore;

import com.google.inject.ImplementedBy;
import org.iatoki.judgels.play.model.JudgelsDao;

@ImplementedBy(ProblemScoreStatisticHibernateDao.class)
public interface ProblemScoreStatisticDao extends JudgelsDao<ProblemScoreStatisticModel> {

}
