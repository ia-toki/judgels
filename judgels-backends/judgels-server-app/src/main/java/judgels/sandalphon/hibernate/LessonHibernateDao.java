package judgels.sandalphon.hibernate;

import static judgels.persistence.CustomPredicateFilter.or;
import static judgels.sandalphon.hibernate.LessonPartnerHibernateDao.hasPartner;

import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import judgels.persistence.CustomPredicateFilter;
import judgels.persistence.FilterOptions;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.JudgelsHibernateDao;
import judgels.sandalphon.persistence.LessonDao;
import judgels.sandalphon.persistence.LessonModel;
import judgels.sandalphon.persistence.LessonModel_;

public final class LessonHibernateDao extends JudgelsHibernateDao<LessonModel> implements LessonDao {

    @Inject
    public LessonHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public Page<LessonModel> selectPaged(String termFilter, SelectionOptions options) {
        return selectPaged(new FilterOptions.Builder<LessonModel>()
                .putColumnsLike(LessonModel_.slug, termFilter)
                .putColumnsLike(LessonModel_.additionalNote, termFilter)
                .build(), options);
    }

    @Override
    public Page<LessonModel> selectPagedByUserJid(String userJid, String termFilter, SelectionOptions options) {
        return selectPaged(new FilterOptions.Builder<LessonModel>()
                .addCustomPredicates(isVisible(userJid))
                .putColumnsLike(LessonModel_.slug, termFilter)
                .putColumnsLike(LessonModel_.additionalNote, termFilter)
                .build(), options);
    }

    @Override
    public LessonModel findBySlug(String slug) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<LessonModel> query = cb.createQuery(LessonModel.class);
        Root<LessonModel> root = query.from(getEntityClass());

        query.where(cb.equal(root.get(LessonModel_.slug), slug));

        return currentSession().createQuery(query).getSingleResult();
    }

    @Override
    public boolean existsBySlug(String slug) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<LessonModel> root = query.from(getEntityClass());

        query
                .select(cb.count(root))
                .where(cb.equal(root.get(LessonModel_.slug), slug));

        return currentSession().createQuery(query).getSingleResult() > 0;
    }

    static CustomPredicateFilter<LessonModel> isVisible(String userJid) {
        return or(
                hasAuthor(userJid),
                hasPartner(userJid));
    }

    static CustomPredicateFilter<LessonModel> hasAuthor(String userJid) {
        return (cb, cq, root) -> cb.equal(root.get(LessonModel_.createdBy), userJid);
    }
}
