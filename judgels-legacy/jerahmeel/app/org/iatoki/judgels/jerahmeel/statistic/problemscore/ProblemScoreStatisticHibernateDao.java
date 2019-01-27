package org.iatoki.judgels.jerahmeel.statistic.problemscore;

import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.JudgelsHibernateDao;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class ProblemScoreStatisticHibernateDao extends JudgelsHibernateDao<ProblemScoreStatisticModel> implements ProblemScoreStatisticDao {

    @Inject
    public ProblemScoreStatisticHibernateDao(HibernateDaoData data) {
        super(data);
    }
}
