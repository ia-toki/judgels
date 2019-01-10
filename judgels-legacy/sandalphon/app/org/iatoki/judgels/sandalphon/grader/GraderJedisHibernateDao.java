package org.iatoki.judgels.sandalphon.grader;

import com.google.common.collect.ImmutableList;
import org.iatoki.judgels.play.model.AbstractJudgelsJedisHibernateDao;
import redis.clients.jedis.JedisPool;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.metamodel.SingularAttribute;
import java.util.List;

@Singleton
public final class GraderJedisHibernateDao extends AbstractJudgelsJedisHibernateDao<GraderModel> implements GraderDao {

    @Inject
    public GraderJedisHibernateDao(JedisPool jedisPool) {
        super(jedisPool, GraderModel.class);
    }

    @Override
    protected List<SingularAttribute<GraderModel, String>> getColumnsFilterableByString() {
        return ImmutableList.of(GraderModel_.name);
    }
}
