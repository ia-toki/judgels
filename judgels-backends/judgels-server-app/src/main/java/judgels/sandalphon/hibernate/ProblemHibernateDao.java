package judgels.sandalphon.hibernate;

import static judgels.persistence.CustomPredicateFilter.or;
import static judgels.sandalphon.hibernate.ProblemPartnerHibernateDao.hasPartner;
import static judgels.sandalphon.hibernate.ProblemTagHibernateDao.hasTagsMatching;

import java.util.List;
import java.util.Set;
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
import judgels.sandalphon.persistence.ProblemDao;
import judgels.sandalphon.persistence.ProblemModel;
import judgels.sandalphon.persistence.ProblemModel_;

public final class ProblemHibernateDao extends JudgelsHibernateDao<ProblemModel> implements ProblemDao {
    @Inject
    public ProblemHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public Page<ProblemModel> selectPaged(String termFilter, List<Set<String>> tagsFilterByType, SelectionOptions options) {
        return selectPaged(new FilterOptions.Builder<ProblemModel>()
                .putColumnsLike(ProblemModel_.slug, termFilter)
                .putColumnsLike(ProblemModel_.additionalNote, termFilter)
                .addCustomPredicates(hasTagsMatching(tagsFilterByType))
                .build(), options);
    }

    @Override
    public Page<ProblemModel> selectPagedByUserJid(String userJid, String termFilter, List<Set<String>> tagsFilterByType, SelectionOptions options) {
        return selectPaged(new FilterOptions.Builder<ProblemModel>()
                .addCustomPredicates(isVisible(userJid))
                .putColumnsLike(ProblemModel_.slug, termFilter)
                .putColumnsLike(ProblemModel_.additionalNote, termFilter)
                .addCustomPredicates(hasTagsMatching(tagsFilterByType))
                .build(), options);
    }

    @Override
    public List<String> getJidsByAuthorJid(String authorJid) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<String> query = cb.createQuery(String.class);
        Root<ProblemModel> root = query.from(getEntityClass());

        query
                .select(root.get(ProblemModel_.jid))
                .where(cb.equal(root.get(ProblemModel_.createdBy), authorJid));

        return currentSession().createQuery(query).getResultList();
    }

    @Override
    public ProblemModel findBySlug(String slug) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<ProblemModel> query = cb.createQuery(ProblemModel.class);
        Root<ProblemModel> root = query.from(getEntityClass());

        query.where(cb.equal(root.get(ProblemModel_.slug), slug));

        return currentSession().createQuery(query).getSingleResult();
    }

    @Override
    public boolean existsBySlug(String slug) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<ProblemModel> root = query.from(getEntityClass());

        query
                .select(cb.count(root))
                .where(cb.equal(root.get(ProblemModel_.slug), slug));

        return currentSession().createQuery(query).getSingleResult() > 0;
    }

    static CustomPredicateFilter<ProblemModel> isVisible(String userJid) {
        return or(
                hasAuthor(userJid),
                hasPartner(userJid));
    }

    static CustomPredicateFilter<ProblemModel> hasAuthor(String userJid) {
        return (cb, cq, root) -> cb.equal(root.get(ProblemModel_.createdBy), userJid);
    }
}
