package org.iatoki.judgels.jerahmeel.statistic.problem;

import com.google.inject.ImplementedBy;
import judgels.persistence.JudgelsDao;

@ImplementedBy(ProblemStatisticHibernateDao.class)
public interface ProblemStatisticDao extends JudgelsDao<ProblemStatisticModel> {

}
