package judgels.persistence.dao;

import jakarta.inject.Inject;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import java.util.Collection;
import java.util.Optional;
import judgels.persistence.CriteriaPredicate;
import judgels.persistence.DaoData;
import judgels.persistence.JudgelsDao;
import judgels.persistence.QueryBuilder;
import judgels.persistence.model.LessonModel;
import judgels.persistence.model.LessonModel_;
import judgels.persistence.model.LessonPartnerModel;
import judgels.persistence.model.LessonPartnerModel_;
import org.hibernate.Session;

public final class LessonDao extends JudgelsDao<LessonModel> {
    @Inject
    public LessonDao(DaoData data) {
        super(data);
    }

    @Override
    public LessonQueryBuilder select() {
        return new LessonQueryBuilder(currentSession());
    }

    public Optional<LessonModel> selectBySlug(String slug) {
        return select().where(columnEq(LessonModel_.slug, slug)).unique();
    }

    public static class LessonQueryBuilder extends QueryBuilder<LessonModel> {
        LessonQueryBuilder(Session currentSession) {
            super(currentSession, LessonModel.class);
        }

        public LessonQueryBuilder whereUserCanView(String userJid) {
            where(CriteriaPredicate.or(
                    userIsAuthor(userJid),
                    userIsPartner(userJid)));
            return this;
        }

        public LessonQueryBuilder whereTermsMatch(String term) {
            where(CriteriaPredicate.or(
                    columnLike(LessonModel_.slug, term),
                    columnLike(LessonModel_.additionalNote, term)));
            return this;
        }

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
