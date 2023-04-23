package judgels.sandalphon.hibernate;

import static judgels.persistence.CustomPredicateFilter.or;
import static judgels.sandalphon.hibernate.ProblemPartnerHibernateDao.hasPartner;
import static judgels.sandalphon.hibernate.ProblemTagHibernateDao.hasTagsMatching;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;
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
    public Optional<ProblemModel> selectBySlug(String slug) {
        return selectByFilter(new FilterOptions.Builder<ProblemModel>()
                .putColumnsEq(ProblemModel_.slug, slug)
                .build());
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

    static CustomPredicateFilter<ProblemModel> isVisible(String userJid) {
        return or(
                hasAuthor(userJid),
                hasPartner(userJid));
    }

    static CustomPredicateFilter<ProblemModel> hasAuthor(String userJid) {
        return (cb, cq, root) -> cb.equal(root.get(ProblemModel_.createdBy), userJid);
    }
}
