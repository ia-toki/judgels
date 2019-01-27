package org.iatoki.judgels.jerahmeel.archive;

import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.JudgelsHibernateDao;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class ArchiveHibernateDao extends JudgelsHibernateDao<ArchiveModel> implements ArchiveDao {

    @Inject
    public ArchiveHibernateDao(HibernateDaoData data) {
        super(data);
    }
}
