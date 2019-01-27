package org.iatoki.judgels.sandalphon.client;

import com.google.common.collect.ImmutableList;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.JudgelsHibernateDao;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.metamodel.SingularAttribute;
import java.util.List;

@Singleton
public final class ClientHibernateDao extends JudgelsHibernateDao<ClientModel> implements ClientDao {

    @Inject
    public ClientHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    protected List<SingularAttribute<ClientModel, String>> getColumnsFilterableByString() {
        return ImmutableList.of(ClientModel_.name);
    }
}
