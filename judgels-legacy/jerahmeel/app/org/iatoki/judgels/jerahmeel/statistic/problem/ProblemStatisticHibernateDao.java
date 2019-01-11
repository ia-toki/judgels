package org.iatoki.judgels.jerahmeel.statistic.problem;

import org.iatoki.judgels.play.model.AbstractJudgelsHibernateDao;

import javax.inject.Singleton;

@Singleton
public final class ProblemStatisticHibernateDao extends AbstractJudgelsHibernateDao<ProblemStatisticModel> implements ProblemStatisticDao {

    public ProblemStatisticHibernateDao() {
        super(ProblemStatisticModel.class);
    }
}
