package org.iatoki.judgels.jerahmeel.statistic.point;

import org.iatoki.judgels.play.model.AbstractHibernateDao;

import javax.inject.Singleton;

@Singleton
public final class PointStatisticEntryHibernateDao extends AbstractHibernateDao<Long, PointStatisticEntryModel> implements PointStatisticEntryDao {

    public PointStatisticEntryHibernateDao() {
        super(PointStatisticEntryModel.class);
    }
}
