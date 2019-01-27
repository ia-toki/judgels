package org.iatoki.judgels.sandalphon.activity;

import judgels.persistence.hibernate.HibernateDaoData;
import org.iatoki.judgels.jophiel.activity.AbstractActivityLogHibernateDao;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class ActivityLogHibernateDao extends AbstractActivityLogHibernateDao<ActivityLogModel> implements ActivityLogDao {

    @Inject
    public ActivityLogHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public ActivityLogModel createActivityLogModel() {
        return new ActivityLogModel();
    }
}
