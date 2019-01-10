package org.iatoki.judgels.sandalphon.problem.bundle.grading;

import redis.clients.jedis.JedisPool;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class BundleGradingJedisHibernateDao extends AbstractBundleGradingJedisHibernateDao<BundleGradingModel> implements BundleGradingDao {

    @Inject
    public BundleGradingJedisHibernateDao(JedisPool jedisPool) {
        super(jedisPool, BundleGradingModel.class);
    }

    @Override
    public BundleGradingModel createGradingModel() {
        return new BundleGradingModel();
    }
}
