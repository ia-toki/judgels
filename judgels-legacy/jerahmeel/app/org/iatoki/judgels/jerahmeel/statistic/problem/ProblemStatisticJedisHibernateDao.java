package org.iatoki.judgels.jerahmeel.statistic.problem;

import org.iatoki.judgels.play.model.AbstractJudgelsJedisHibernateDao;
import redis.clients.jedis.JedisPool;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class ProblemStatisticJedisHibernateDao extends AbstractJudgelsJedisHibernateDao<ProblemStatisticModel> implements ProblemStatisticDao {

    @Inject
    public ProblemStatisticJedisHibernateDao(JedisPool jedisPool) {
        super(jedisPool, ProblemStatisticModel.class);
    }
}
