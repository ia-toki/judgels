package org.iatoki.judgels.jerahmeel.statistic.problemscore;

import org.iatoki.judgels.play.model.AbstractJudgelsJedisHibernateDao;
import redis.clients.jedis.JedisPool;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class ProblemScoreStatisticJedisHibernateDao extends AbstractJudgelsJedisHibernateDao<ProblemScoreStatisticModel> implements ProblemScoreStatisticDao {

    @Inject
    public ProblemScoreStatisticJedisHibernateDao(JedisPool jedisPool) {
        super(jedisPool, ProblemScoreStatisticModel.class);
    }
}
