package org.iatoki.judgels.jerahmeel.statistic.point;

import com.google.inject.ImplementedBy;
import judgels.persistence.Dao;

@ImplementedBy(PointStatisticEntryHibernateDao.class)
public interface PointStatisticEntryDao extends Dao<PointStatisticEntryModel> {

}
