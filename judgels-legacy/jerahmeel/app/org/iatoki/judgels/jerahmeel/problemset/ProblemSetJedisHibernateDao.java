package org.iatoki.judgels.jerahmeel.problemset;

import org.iatoki.judgels.play.model.AbstractJudgelsJedisHibernateDao;
import redis.clients.jedis.JedisPool;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class ProblemSetJedisHibernateDao extends AbstractJudgelsJedisHibernateDao<ProblemSetModel> implements ProblemSetDao {

    @Inject
    public ProblemSetJedisHibernateDao(JedisPool jedisPool) {
        super(jedisPool, ProblemSetModel.class);
    }
}
