package org.iatoki.judgels.jerahmeel.statistic.point;

import com.google.inject.ImplementedBy;
import org.iatoki.judgels.play.model.Dao;

@ImplementedBy(PointStatisticEntryHibernateDao.class)
public interface PointStatisticEntryDao extends Dao<Long, PointStatisticEntryModel> {

}
