package org.iatoki.judgels.jerahmeel.statistic.point;

import com.google.inject.ImplementedBy;
import org.iatoki.judgels.play.model.JudgelsDao;

@ImplementedBy(PointStatisticHibernateDao.class)
public interface PointStatisticDao extends JudgelsDao<PointStatisticModel> {

}
