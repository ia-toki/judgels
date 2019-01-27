package org.iatoki.judgels.jerahmeel.statistic.problemscore;

import judgels.persistence.hibernate.HibernateDao;
import judgels.persistence.hibernate.HibernateDaoData;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class ProblemScoreStatisticEntryHibernateDao extends HibernateDao<ProblemScoreStatisticEntryModel> implements ProblemScoreStatisticEntryDao {

    @Inject
    public ProblemScoreStatisticEntryHibernateDao(HibernateDaoData data) {
        super(data);
    }
}
