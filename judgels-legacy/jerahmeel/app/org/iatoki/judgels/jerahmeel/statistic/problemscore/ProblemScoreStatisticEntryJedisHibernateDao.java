package org.iatoki.judgels.jerahmeel.statistic.problemscore;

import org.iatoki.judgels.play.model.AbstractJedisHibernateDao;
import redis.clients.jedis.JedisPool;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class ProblemScoreStatisticEntryJedisHibernateDao extends AbstractJedisHibernateDao<Long, ProblemScoreStatisticEntryModel> implements ProblemScoreStatisticEntryDao {

    @Inject
    public ProblemScoreStatisticEntryJedisHibernateDao(JedisPool jedisPool) {
        super(jedisPool, ProblemScoreStatisticEntryModel.class);
    }
}
