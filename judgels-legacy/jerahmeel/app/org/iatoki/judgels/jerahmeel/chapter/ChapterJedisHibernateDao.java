package org.iatoki.judgels.jerahmeel.chapter;

import com.google.common.collect.ImmutableList;
import org.iatoki.judgels.play.model.AbstractJudgelsJedisHibernateDao;
import redis.clients.jedis.JedisPool;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.metamodel.SingularAttribute;
import java.util.List;

@Singleton
public final class ChapterJedisHibernateDao extends AbstractJudgelsJedisHibernateDao<ChapterModel> implements ChapterDao {

    @Inject
    public ChapterJedisHibernateDao(JedisPool jedisPool) {
        super(jedisPool, ChapterModel.class);
    }

    @Override
    protected List<SingularAttribute<ChapterModel, String>> getColumnsFilterableByString() {
        return ImmutableList.of(ChapterModel_.name, ChapterModel_.description);
    }
}
