package org.iatoki.judgels.jerahmeel.statistic.problemscore;

import org.iatoki.judgels.play.model.AbstractHibernateDao;

import javax.inject.Singleton;

@Singleton
public final class ProblemScoreStatisticEntryHibernateDao extends AbstractHibernateDao<Long, ProblemScoreStatisticEntryModel> implements ProblemScoreStatisticEntryDao {

    public ProblemScoreStatisticEntryHibernateDao() {
        super(ProblemScoreStatisticEntryModel.class);
    }
}
