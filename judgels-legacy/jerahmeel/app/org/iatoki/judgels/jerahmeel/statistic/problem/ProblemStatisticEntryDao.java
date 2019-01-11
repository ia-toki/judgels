package org.iatoki.judgels.jerahmeel.statistic.problem;

import com.google.inject.ImplementedBy;
import org.iatoki.judgels.play.model.Dao;

@ImplementedBy(ProblemStatisticEntryHibernateDao.class)
public interface ProblemStatisticEntryDao extends Dao<Long, ProblemStatisticEntryModel> {

}
