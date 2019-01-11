package org.iatoki.judgels.jerahmeel.statistic.problem;

import org.iatoki.judgels.play.model.AbstractJedisHibernateDao;
import redis.clients.jedis.JedisPool;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class ProblemStatisticEntryJedisHibernateDao extends AbstractJedisHibernateDao<Long, ProblemStatisticEntryModel> implements ProblemStatisticEntryDao {

    @Inject
    public ProblemStatisticEntryJedisHibernateDao(JedisPool jedisPool) {
        super(jedisPool, ProblemStatisticEntryModel.class);
    }
}
