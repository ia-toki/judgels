package org.iatoki.judgels.jerahmeel.statistic.point;

import org.iatoki.judgels.play.model.AbstractJudgelsHibernateDao;

import javax.inject.Singleton;

@Singleton
public final class PointStatisticHibernateDao extends AbstractJudgelsHibernateDao<PointStatisticModel> implements PointStatisticDao {

    public PointStatisticHibernateDao() {
        super(PointStatisticModel.class);
    }
}
