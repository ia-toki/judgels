package org.iatoki.judgels.jophiel.activity;

import judgels.persistence.ActorProvider;
import judgels.persistence.hibernate.HibernateDao;
import org.hibernate.SessionFactory;

import java.time.Clock;

public abstract class AbstractActivityLogHibernateDao<M extends AbstractActivityLogModel> extends HibernateDao<M> implements BaseActivityLogDao<M> {
    public AbstractActivityLogHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
    }
}
