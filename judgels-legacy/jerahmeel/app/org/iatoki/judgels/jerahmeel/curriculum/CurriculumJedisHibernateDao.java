package org.iatoki.judgels.jerahmeel.curriculum;

import org.iatoki.judgels.play.model.AbstractJudgelsJedisHibernateDao;
import redis.clients.jedis.JedisPool;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class CurriculumJedisHibernateDao extends AbstractJudgelsJedisHibernateDao<CurriculumModel> implements CurriculumDao {

    @Inject
    public CurriculumJedisHibernateDao(JedisPool jedisPool) {
        super(jedisPool, CurriculumModel.class);
    }

}
