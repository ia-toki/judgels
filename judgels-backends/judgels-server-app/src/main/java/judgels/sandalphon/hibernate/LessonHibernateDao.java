package judgels.sandalphon.hibernate;

import java.util.Collection;
import java.util.Optional;
import javax.inject.Inject;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import judgels.persistence.CriteriaPredicate;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.HibernateQueryBuilder;
import judgels.persistence.hibernate.JudgelsHibernateDao;
import judgels.sandalphon.persistence.LessonDao;
import judgels.sandalphon.persistence.LessonModel;
import judgels.sandalphon.persistence.LessonModel_;
import judgels.sandalphon.persistence.LessonPartnerModel;
import judgels.sandalphon.persistence.LessonPartnerModel_;
import org.hibernate.Session;

public final class LessonHibernateDao extends JudgelsHibernateDao<LessonModel> implements LessonDao {
    @Inject
    public LessonHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public LessonHibernateQueryBuilder select() {
        return new LessonHibernateQueryBuilder(currentSession());
    }

    @Override
    public Optional<LessonModel> selectBySlug(String slug) {
        return select().where(columnEq(LessonModel_.slug, slug)).unique();
    }

    private static class LessonHibernateQueryBuilder extends HibernateQueryBuilder<LessonModel> implements LessonQueryBuilder {
        LessonHibernateQueryBuilder(Session currentSession) {
            super(currentSession, LessonModel.class);
        }

        @Override
        public LessonQueryBuilder whereUserCanView(String userJid) {
            where(CriteriaPredicate.or(
                    userIsAuthor(userJid),
                    userIsPartner(userJid)));
            return this;
        }

        @Override
        public LessonQueryBuilder whereTermsMatch(String term) {
            where(CriteriaPredicate.or(
                    columnLike(LessonModel_.slug, term),
                    columnLike(LessonModel_.additionalNote, term)));
            return this;
        }

        @Override
        public LessonQueryBuilder whereSlugIn(Collection<String> slugs) {
            where(columnIn(LessonModel_.slug, slugs));
            return this;
        }

        private CriteriaPredicate<LessonModel> userIsAuthor(String userJid) {
            return (cb, cq, root) -> cb.equal(root.get(LessonModel_.createdBy), userJid);
        }

        private CriteriaPredicate<LessonModel> userIsPartner(String userJid) {
            return (cb, cq, root) -> {
                Subquery<LessonPartnerModel> subquery = cq.subquery(LessonPartnerModel.class);
                Root<LessonPartnerModel> subroot = subquery.from(LessonPartnerModel.class);

                return cb.exists(subquery
                        .select(subroot)
                        .where(
                                cb.equal(subroot.get(LessonPartnerModel_.lessonJid), root.get(LessonModel_.jid)),
                                cb.equal(subroot.get(LessonPartnerModel_.userJid), userJid)));
            };
        }
    }
}
