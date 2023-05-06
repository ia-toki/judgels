package judgels.sandalphon.hibernate;

import static judgels.persistence.CriteriaPredicate.or;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import judgels.persistence.CriteriaPredicate;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.HibernateQueryBuilder;
import judgels.persistence.hibernate.JudgelsHibernateDao;
import judgels.sandalphon.persistence.ProblemDao;
import judgels.sandalphon.persistence.ProblemModel;
import judgels.sandalphon.persistence.ProblemModel_;
import judgels.sandalphon.persistence.ProblemPartnerModel;
import judgels.sandalphon.persistence.ProblemPartnerModel_;
import judgels.sandalphon.persistence.ProblemTagModel;
import judgels.sandalphon.persistence.ProblemTagModel_;
import org.hibernate.Session;

public final class ProblemHibernateDao extends JudgelsHibernateDao<ProblemModel> implements ProblemDao {
    @Inject
    public ProblemHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public ProblemHibernateQueryBuilder select() {
        return new ProblemHibernateQueryBuilder(currentSession());
    }

    @Override
    public Optional<ProblemModel> selectBySlug(String slug) {
        return select().where(columnEq(ProblemModel_.slug, slug)).unique();
    }

    private static class ProblemHibernateQueryBuilder extends HibernateQueryBuilder<ProblemModel> implements ProblemQueryBuilder {
        ProblemHibernateQueryBuilder(Session currentSession) {
            super(currentSession, ProblemModel.class);
        }

        @Override
        public ProblemQueryBuilder whereUserCanView(String userJid) {
            where(or(
                    userIsAuthor(userJid),
                    userIsPartner(userJid)));
            return this;
        }

        @Override
        public ProblemQueryBuilder whereTermsMatch(String term) {
            where(or(
                    columnLike(ProblemModel_.slug, term),
                    columnLike(ProblemModel_.additionalNote, term)));
            return this;
        }

        @Override
        public ProblemQueryBuilder whereTagsMatch(List<Set<String>> tagGroups) {
            for (Set<String> tagGroup : tagGroups) {
                if (!tagGroup.isEmpty()) {
                    where(tagsIntersect(tagGroup));
                }
            }
            return this;
        }

        @Override
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
