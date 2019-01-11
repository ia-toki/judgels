package org.iatoki.judgels.jerahmeel.statistic.problem;

import com.google.inject.ImplementedBy;
import org.iatoki.judgels.play.model.JudgelsDao;

@ImplementedBy(ProblemStatisticHibernateDao.class)
public interface ProblemStatisticDao extends JudgelsDao<ProblemStatisticModel> {

}
