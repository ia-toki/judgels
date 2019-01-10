package org.iatoki.judgels.jophiel.activity;

import org.iatoki.judgels.play.model.AbstractJedisHibernateDao;
import redis.clients.jedis.JedisPool;

public abstract class AbstractActivityLogJedisHibernateDao<M extends AbstractActivityLogModel> extends AbstractJedisHibernateDao<Long, M> implements BaseActivityLogDao<M> {

    protected AbstractActivityLogJedisHibernateDao(JedisPool jedisPool, Class<M> modelClass) {
        super(jedisPool, modelClass);
    }
}
