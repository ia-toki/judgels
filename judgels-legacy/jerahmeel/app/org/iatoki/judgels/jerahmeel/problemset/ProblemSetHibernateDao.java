package org.iatoki.judgels.jerahmeel.problemset;

import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.JudgelsHibernateDao;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class ProblemSetHibernateDao extends JudgelsHibernateDao<ProblemSetModel> implements ProblemSetDao {

    @Inject
    public ProblemSetHibernateDao(HibernateDaoData data) {
        super(data);
    }
}
