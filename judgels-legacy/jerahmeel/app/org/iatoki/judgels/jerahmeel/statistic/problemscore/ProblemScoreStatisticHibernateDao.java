package org.iatoki.judgels.jerahmeel.statistic.problemscore;

import org.iatoki.judgels.play.model.AbstractJudgelsHibernateDao;

import javax.inject.Singleton;

@Singleton
public final class ProblemScoreStatisticHibernateDao extends AbstractJudgelsHibernateDao<ProblemScoreStatisticModel> implements ProblemScoreStatisticDao {

    public ProblemScoreStatisticHibernateDao() {
        super(ProblemScoreStatisticModel.class);
    }
}
