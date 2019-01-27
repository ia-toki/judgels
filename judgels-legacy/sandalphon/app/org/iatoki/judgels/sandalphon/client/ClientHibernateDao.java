package org.iatoki.judgels.sandalphon.client;

import com.google.common.collect.ImmutableList;
import judgels.persistence.ActorProvider;
import judgels.persistence.hibernate.JudgelsHibernateDao;
import org.hibernate.SessionFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.metamodel.SingularAttribute;
import java.time.Clock;
import java.util.List;

@Singleton
public final class ClientHibernateDao extends JudgelsHibernateDao<ClientModel> implements ClientDao {

    @Inject
    public ClientHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
    }

    @Override
    protected List<SingularAttribute<ClientModel, String>> getColumnsFilterableByString() {
        return ImmutableList.of(ClientModel_.name);
    }
}
