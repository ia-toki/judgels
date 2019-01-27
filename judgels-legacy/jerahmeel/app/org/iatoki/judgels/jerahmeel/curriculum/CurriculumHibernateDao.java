package org.iatoki.judgels.jerahmeel.curriculum;

import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.JudgelsHibernateDao;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class CurriculumHibernateDao extends JudgelsHibernateDao<CurriculumModel> implements CurriculumDao {

    @Inject
    public CurriculumHibernateDao(HibernateDaoData data) {
        super(data);
    }
}
