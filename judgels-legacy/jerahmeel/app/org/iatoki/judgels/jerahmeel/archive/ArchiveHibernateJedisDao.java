package org.iatoki.judgels.jerahmeel.archive;

import org.iatoki.judgels.play.model.AbstractJudgelsJedisHibernateDao;
import redis.clients.jedis.JedisPool;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class ArchiveHibernateJedisDao extends AbstractJudgelsJedisHibernateDao<ArchiveModel> implements ArchiveDao {

    @Inject
    public ArchiveHibernateJedisDao(JedisPool jedisPool) {
        super(jedisPool, ArchiveModel.class);
    }
}
