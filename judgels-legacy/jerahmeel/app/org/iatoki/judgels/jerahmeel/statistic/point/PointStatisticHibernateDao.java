package org.iatoki.judgels.jerahmeel.statistic.point;

import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.JudgelsHibernateDao;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class PointStatisticHibernateDao extends JudgelsHibernateDao<PointStatisticModel> implements PointStatisticDao {

    @Inject
    public PointStatisticHibernateDao(HibernateDaoData data) {
        super(data);
    }
}
