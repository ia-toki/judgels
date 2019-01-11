package org.iatoki.judgels.jerahmeel.grading.programming;

import org.iatoki.judgels.sandalphon.problem.programming.grading.AbstractProgrammingGradingJedisHibernateDao;
import redis.clients.jedis.JedisPool;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class ProgrammingGradingJedisHibernateDao extends AbstractProgrammingGradingJedisHibernateDao<ProgrammingGradingModel> implements ProgrammingGradingDao {

    @Inject
    public ProgrammingGradingJedisHibernateDao(JedisPool jedisPool) {
        super(jedisPool, ProgrammingGradingModel.class);
    }

    @Override
    public ProgrammingGradingModel createGradingModel() {
        return new ProgrammingGradingModel();
    }
}
