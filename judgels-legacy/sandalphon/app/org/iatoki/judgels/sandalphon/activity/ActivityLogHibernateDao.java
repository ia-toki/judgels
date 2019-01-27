package org.iatoki.judgels.sandalphon.activity;

import judgels.persistence.ActorProvider;
import org.hibernate.SessionFactory;
import org.iatoki.judgels.jophiel.activity.AbstractActivityLogHibernateDao;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Clock;

@Singleton
public final class ActivityLogHibernateDao extends AbstractActivityLogHibernateDao<ActivityLogModel> implements ActivityLogDao {

    @Inject
    public ActivityLogHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
    }

    @Override
    public ActivityLogModel createActivityLogModel() {
        return new ActivityLogModel();
    }
}
