package org.iatoki.judgels.jerahmeel.statistic.problem;

import judgels.persistence.hibernate.HibernateDao;
import judgels.persistence.hibernate.HibernateDaoData;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class ProblemStatisticEntryHibernateDao extends HibernateDao<ProblemStatisticEntryModel> implements ProblemStatisticEntryDao {

    @Inject
    public ProblemStatisticEntryHibernateDao(HibernateDaoData data) {
        super(data);
    }
}
