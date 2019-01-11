package org.iatoki.judgels.jerahmeel.course;

import com.google.common.collect.ImmutableList;
import org.iatoki.judgels.play.model.AbstractJudgelsJedisHibernateDao;
import redis.clients.jedis.JedisPool;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.metamodel.SingularAttribute;
import java.util.List;

@Singleton
public final class CourseJedisHibernateDao extends AbstractJudgelsJedisHibernateDao<CourseModel> implements CourseDao {

    @Inject
    public CourseJedisHibernateDao(JedisPool jedisPool) {
        super(jedisPool, CourseModel.class);
    }

    @Override
    protected List<SingularAttribute<CourseModel, String>> getColumnsFilterableByString() {
        return ImmutableList.of(CourseModel_.name, CourseModel_.description);
    }
}
