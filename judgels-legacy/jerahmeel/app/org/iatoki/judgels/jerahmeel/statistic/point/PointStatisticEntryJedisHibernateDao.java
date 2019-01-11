package org.iatoki.judgels.jerahmeel.statistic.point;

import org.iatoki.judgels.play.model.AbstractJedisHibernateDao;
import redis.clients.jedis.JedisPool;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class PointStatisticEntryJedisHibernateDao extends AbstractJedisHibernateDao<Long, PointStatisticEntryModel> implements PointStatisticEntryDao {

    @Inject
    public PointStatisticEntryJedisHibernateDao(JedisPool jedisPool) {
        super(jedisPool, PointStatisticEntryModel.class);
    }
}
