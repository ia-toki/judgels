package org.iatoki.judgels.sandalphon.lesson;

import com.google.common.collect.ImmutableList;
import org.iatoki.judgels.play.model.AbstractJudgelsHibernateDao;
import play.db.jpa.JPA;

import javax.inject.Singleton;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;
import java.util.List;

@Singleton
public final class LessonHibernateDao extends AbstractJudgelsHibernateDao<LessonModel> implements LessonDao {

    public LessonHibernateDao() {
        super(LessonModel.class);
    }

    @Override
    public List<String> getJidsByAuthorJid(String authorJid) {
        CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
        CriteriaQuery<String> query = cb.createQuery(String.class);
        Root<LessonModel> root = query.from(getModelClass());

        query
                .select(root.get(LessonModel_.jid))
                .where(cb.equal(root.get(LessonModel_.userCreate), authorJid));

        return JPA.em().createQuery(query).getResultList();
    }

    @Override
    public boolean existsBySlug(String slug) {
        CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<LessonModel> root = query.from(getModelClass());

        query
                .select(cb.count(root))
                .where(cb.equal(root.get(LessonModel_.slug), slug));

        return JPA.em().createQuery(query).getSingleResult() > 0;
    }

    @Override
    protected List<SingularAttribute<LessonModel, String>> getColumnsFilterableByString() {
        return ImmutableList.of(LessonModel_.slug, LessonModel_.additionalNote);
    }
}
