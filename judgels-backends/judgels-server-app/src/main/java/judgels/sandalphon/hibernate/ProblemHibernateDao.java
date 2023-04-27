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
        return new ProblemHibernateQueryBuilder(currentSession(), ProblemModel.class);
    }

    @Override
    public Optional<ProblemModel> selectUniqueBySlug(String slug) {
        return selectByUniqueColumn(ProblemModel_.slug, slug);
    }

    private static class ProblemHibernateQueryBuilder extends HibernateQueryBuilder<ProblemModel> implements ProblemQueryBuilder {
        ProblemHibernateQueryBuilder(Session currentSession, Class<ProblemModel> entityClass) {
            super(currentSession, entityClass);
        }

        @Override
        public ProblemQueryBuilder whereUserCanView(String userJid, boolean isAdmin) {
            if (!isAdmin) {
                where(or(
                        userIsAuthor(userJid),
                        userIsPartner(userJid)));
            }
            return this;
        }

        @Override
        public ProblemQueryBuilder whereTermsMatch(String term) {
            if (!term.isEmpty()) {
                where(or(
                        columnLike(ProblemModel_.slug, term),
                        columnLike(ProblemModel_.additionalNote, term)));
            }
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

        private CriteriaPredicate<ProblemModel> userIsAuthor(String userJid) {
            return (cb, cq, root) -> cb.equal(root.get(ProblemModel_.createdBy), userJid);
        }

        private CriteriaPredicate<ProblemModel> userIsPartner(String userJid) {
            return (cb, cq, root) -> {
                Subquery<ProblemPartnerModel> sq = cq.subquery(ProblemPartnerModel.class);
                Root<ProblemPartnerModel> subRoot = sq.from(ProblemPartnerModel.class);

                return cb.exists(sq
                        .select(subRoot)
                        .where(
                                cb.equal(subRoot.get(ProblemPartnerModel_.problemJid), root.get(ProblemModel_.jid)),
                                cb.equal(subRoot.get(ProblemPartnerModel_.userJid), userJid)));
            };
        }

        private CriteriaPredicate<ProblemModel> tagsIntersect(Set<String> tags) {
            return (cb, cq, root) -> {
                Subquery<ProblemTagModel> sq = cq.subquery(ProblemTagModel.class);
                Root<ProblemTagModel> subRoot = sq.from(ProblemTagModel.class);

                return cb.exists(sq
                        .select(subRoot)
                        .where(
                                cb.equal(subRoot.get(ProblemTagModel_.problemJid), root.get(ProblemModel_.jid)),
                                subRoot.get(ProblemTagModel_.tag).in(tags)));
            };
        }
    }
}
