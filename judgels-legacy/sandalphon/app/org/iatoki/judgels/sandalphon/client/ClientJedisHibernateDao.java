package org.iatoki.judgels.sandalphon.client;

import com.google.common.collect.ImmutableList;
import org.iatoki.judgels.play.model.AbstractJudgelsJedisHibernateDao;
import redis.clients.jedis.JedisPool;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.metamodel.SingularAttribute;
import java.util.List;

@Singleton
public final class ClientJedisHibernateDao extends AbstractJudgelsJedisHibernateDao<ClientModel> implements ClientDao {

    @Inject
    public ClientJedisHibernateDao(JedisPool jedisPool) {
        super(jedisPool, ClientModel.class);
    }

    @Override
    protected List<SingularAttribute<ClientModel, String>> getColumnsFilterableByString() {
        return ImmutableList.of(ClientModel_.name);
    }
}
