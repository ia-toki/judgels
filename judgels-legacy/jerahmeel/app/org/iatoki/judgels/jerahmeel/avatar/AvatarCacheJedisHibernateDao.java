package org.iatoki.judgels.jerahmeel.avatar;

import org.iatoki.judgels.jophiel.avatar.AbstractAvatarCacheJedisHibernateDao;
import redis.clients.jedis.JedisPool;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class AvatarCacheJedisHibernateDao extends AbstractAvatarCacheJedisHibernateDao<AvatarCacheModel> implements AvatarCacheDao {

    @Inject
    public AvatarCacheJedisHibernateDao(JedisPool jedisPool) {
        super(jedisPool, AvatarCacheModel.class);
    }

    @Override
    public AvatarCacheModel createAvatarCacheModel() {
        return new AvatarCacheModel();
    }
}
