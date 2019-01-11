package org.iatoki.judgels.jerahmeel.statistic.problem;

import org.iatoki.judgels.play.model.AbstractHibernateDao;

import javax.inject.Singleton;

@Singleton
public final class ProblemStatisticEntryHibernateDao extends AbstractHibernateDao<Long, ProblemStatisticEntryModel> implements ProblemStatisticEntryDao {

    public ProblemStatisticEntryHibernateDao() {
        super(ProblemStatisticEntryModel.class);
    }
}
