package org.iatoki.judgels.jerahmeel.statistic.point;

import judgels.persistence.hibernate.HibernateDao;
import judgels.persistence.hibernate.HibernateDaoData;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class PointStatisticEntryHibernateDao extends HibernateDao<PointStatisticEntryModel> implements PointStatisticEntryDao {

    @Inject
    public PointStatisticEntryHibernateDao(HibernateDaoData data) {
        super(data);
    }
}
