package org.iatoki.judgels.sandalphon.jid;

import judgels.persistence.ActorProvider;
import org.hibernate.SessionFactory;
import org.iatoki.judgels.play.jid.AbstractJidCacheHibernateDao;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Clock;

@Singleton
public final class JidCacheHibernateDao extends AbstractJidCacheHibernateDao<JidCacheModel> implements JidCacheDao {

    @Inject
    public JidCacheHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
    }

    @Override
    public JidCacheModel createJidCacheModel() {
        return new JidCacheModel();
    }
}
