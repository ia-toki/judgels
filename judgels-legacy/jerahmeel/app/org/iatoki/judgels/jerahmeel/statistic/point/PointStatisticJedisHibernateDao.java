package org.iatoki.judgels.jerahmeel.statistic.point;

import org.iatoki.judgels.play.model.AbstractJudgelsJedisHibernateDao;
import redis.clients.jedis.JedisPool;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class PointStatisticJedisHibernateDao extends AbstractJudgelsJedisHibernateDao<PointStatisticModel> implements PointStatisticDao {

    @Inject
    public PointStatisticJedisHibernateDao(JedisPool jedisPool) {
        super(jedisPool, PointStatisticModel.class);
    }
}
