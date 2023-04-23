package judgels.sandalphon.hibernate;

import static judgels.persistence.CustomPredicateFilter.or;
import static judgels.sandalphon.hibernate.LessonPartnerHibernateDao.hasPartner;

import java.util.Optional;
import javax.inject.Inject;
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
    public Optional<LessonModel> selectBySlug(String slug) {
        return selectByFilter(new FilterOptions.Builder<LessonModel>()
                .putColumnsEq(LessonModel_.slug, slug)
                .build());
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

    static CustomPredicateFilter<LessonModel> isVisible(String userJid) {
        return or(
                hasAuthor(userJid),
                hasPartner(userJid));
    }

    static CustomPredicateFilter<LessonModel> hasAuthor(String userJid) {
        return (cb, cq, root) -> cb.equal(root.get(LessonModel_.createdBy), userJid);
    }
}
