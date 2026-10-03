package judgels.persistence.dao;

import static judgels.persistence.CriteriaPredicate.or;

import jakarta.inject.Inject;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import judgels.persistence.CriteriaPredicate;
import judgels.persistence.DaoData;
import judgels.persistence.JudgelsDao;
import judgels.persistence.QueryBuilder;
import judgels.persistence.model.ProblemModel;
import judgels.persistence.model.ProblemModel_;
import judgels.persistence.model.ProblemPartnerModel;
import judgels.persistence.model.ProblemPartnerModel_;
import judgels.persistence.model.ProblemTagModel;
import judgels.persistence.model.ProblemTagModel_;
import org.hibernate.Session;

public final class ProblemDao extends JudgelsDao<ProblemModel> {
    @Inject
    public ProblemDao(DaoData data) {
        super(data);
    }

    @Override
    public ProblemQueryBuilder select() {
        return new ProblemQueryBuilder(currentSession());
    }

    public Optional<ProblemModel> selectBySlug(String slug) {
        return select().where(columnEq(ProblemModel_.slug, slug)).unique();
    }

    public static class ProblemQueryBuilder extends QueryBuilder<ProblemModel> {
        ProblemQueryBuilder(Session currentSession) {
            super(currentSession, ProblemModel.class);
        }

        public ProblemQueryBuilder whereUserCanView(String userJid) {
            where(or(
                    userIsAuthor(userJid),
                    userIsPartner(userJid)));
            return this;
        }

        public ProblemQueryBuilder whereTermsMatch(String term) {
            where(or(
                    columnLike(ProblemModel_.slug, term),
                    columnLike(ProblemModel_.additionalNote, term)));
            return this;
        }

        public ProblemQueryBuilder whereTagsMatch(List<Set<String>> tagGroups) {
            for (Set<String> tagGroup : tagGroups) {
                if (!tagGroup.isEmpty()) {
                    where(tagsIntersect(tagGroup));
                }
            }
            return this;
        }

        public ProblemQueryBuilder whereSlugIn(Set<String> slugs) {
            where(columnIn(ProblemModel_.slug, slugs));
            return this;
        }

        private CriteriaPredicate<ProblemModel> userIsAuthor(String userJid) {
            return (cb, cq, root) -> cb.equal(root.get(ProblemModel_.createdBy), userJid);
        }

        private CriteriaPredicate<ProblemModel> userIsPartner(String userJid) {
            return (cb, cq, root) -> {
                Subquery<ProblemPartnerModel> subquery = cq.subquery(ProblemPartnerModel.class);
                Root<ProblemPartnerModel> subroot = subquery.from(ProblemPartnerModel.class);

                return cb.exists(subquery
                        .select(subroot)
                        .where(
                                cb.equal(subroot.get(ProblemPartnerModel_.problemJid), root.get(ProblemModel_.jid)),
                                cb.equal(subroot.get(ProblemPartnerModel_.userJid), userJid)));
            };
        }

        private CriteriaPredicate<ProblemModel> tagsIntersect(Set<String> tags) {
            return (cb, cq, root) -> {
                Subquery<ProblemTagModel> subquery = cq.subquery(ProblemTagModel.class);
                Root<ProblemTagModel> subroot = subquery.from(ProblemTagModel.class);

                return cb.exists(subquery
                        .select(subroot)
                        .where(
                                cb.equal(subroot.get(ProblemTagModel_.problemJid), root.get(ProblemModel_.jid)),
                                subroot.get(ProblemTagModel_.tag).in(tags)));
            };
        }
    }
}
