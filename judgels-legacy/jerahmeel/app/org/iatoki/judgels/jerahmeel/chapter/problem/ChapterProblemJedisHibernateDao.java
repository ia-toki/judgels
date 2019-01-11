package org.iatoki.judgels.jerahmeel.chapter.problem;

import org.iatoki.judgels.play.model.AbstractJedisHibernateDao;
import play.db.jpa.JPA;
import redis.clients.jedis.JedisPool;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Singleton
public final class ChapterProblemJedisHibernateDao extends AbstractJedisHibernateDao<Long, ChapterProblemModel> implements ChapterProblemDao {

    @Inject
    public ChapterProblemJedisHibernateDao(JedisPool jedisPool) {
        super(jedisPool, ChapterProblemModel.class);
    }

    @Override
    public boolean existsByChapterJidAndAlias(String chapterJid, String alias) {
        CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<ChapterProblemModel> root = query.from(ChapterProblemModel.class);

        query.select(cb.count(root)).where(cb.and(cb.equal(root.get(ChapterProblemModel_.chapterJid), chapterJid), cb.equal(root.get(ChapterProblemModel_.alias), alias)));

        return (JPA.em().createQuery(query).getSingleResult() != 0);
    }

    @Override
    public List<ChapterProblemModel> getByChapterJid(String chapterJid) {
        CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
        CriteriaQuery<ChapterProblemModel> query = cb.createQuery(ChapterProblemModel.class);
        Root<ChapterProblemModel> root = query.from(ChapterProblemModel.class);

        query.where(cb.equal(root.get(ChapterProblemModel_.chapterJid), chapterJid));

        return JPA.em().createQuery(query).getResultList();
    }

    @Override
    public ChapterProblemModel findByChapterJidAndProblemJid(String chapterJid, String problemJid) {
        CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
        CriteriaQuery<ChapterProblemModel> query = cb.createQuery(ChapterProblemModel.class);
        Root<ChapterProblemModel> root = query.from(ChapterProblemModel.class);

        query.where(cb.and(cb.equal(root.get(ChapterProblemModel_.chapterJid), chapterJid), cb.equal(root.get(ChapterProblemModel_.problemJid), problemJid)));

        return JPA.em().createQuery(query).getSingleResult();
    }
}
