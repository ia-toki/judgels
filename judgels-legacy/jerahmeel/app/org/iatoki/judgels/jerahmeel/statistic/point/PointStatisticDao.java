package org.iatoki.judgels.jerahmeel.statistic.point;

import com.google.inject.ImplementedBy;
import judgels.persistence.JudgelsDao;

@ImplementedBy(PointStatisticHibernateDao.class)
public interface PointStatisticDao extends JudgelsDao<PointStatisticModel> {

}
