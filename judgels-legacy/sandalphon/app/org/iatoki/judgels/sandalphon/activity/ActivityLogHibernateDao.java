package org.iatoki.judgels.sandalphon.activity;

import org.iatoki.judgels.jophiel.activity.AbstractActivityLogHibernateDao;

import javax.inject.Singleton;

@Singleton
public final class ActivityLogHibernateDao extends AbstractActivityLogHibernateDao<ActivityLogModel> implements ActivityLogDao {

    public ActivityLogHibernateDao() {
        super(ActivityLogModel.class);
    }

    @Override
    public ActivityLogModel createActivityLogModel() {
        return new ActivityLogModel();
    }
}
