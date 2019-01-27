package org.iatoki.judgels.jerahmeel.statistic.problem;

import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.JudgelsHibernateDao;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class ProblemStatisticHibernateDao extends JudgelsHibernateDao<ProblemStatisticModel> implements ProblemStatisticDao {

    @Inject
    public ProblemStatisticHibernateDao(HibernateDaoData data) {
        super(data);
    }
}
