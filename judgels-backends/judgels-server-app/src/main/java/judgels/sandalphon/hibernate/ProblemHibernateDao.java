package judgels.sandalphon.hibernate;

import static java.util.stream.Collectors.toList;
import static judgels.persistence.CriteriaPredicate.and;
import static judgels.persistence.CriteriaPredicate.literalTrue;
import static judgels.persistence.CriteriaPredicate.or;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import judgels.persistence.CriteriaPredicate;
import judgels.persistence.FilterOptions;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.JudgelsHibernateDao;
import judgels.sandalphon.persistence.ProblemDao;
import judgels.sandalphon.persistence.ProblemModel;
import judgels.sandalphon.persistence.ProblemModel_;
import judgels.sandalphon.persistence.ProblemPartnerModel;
import judgels.sandalphon.persistence.ProblemPartnerModel_;
import judgels.sandalphon.persistence.ProblemTagModel;
import judgels.sandalphon.persistence.ProblemTagModel_;

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
    public CriteriaPredicate<ProblemModel> userCanView(String userJid, boolean isAdmin) {
        if (isAdmin) {
            return literalTrue();
        }
        return or(
                userIsAuthor(userJid),
                userIsPartner(userJid));
    }

    @Override
    public CriteriaPredicate<ProblemModel> termsMatch(String term) {
        if (term.isEmpty()) {
            return literalTrue();
        }
        return or(
                columnIsLike(ProblemModel_.slug, term),
                columnIsLike(ProblemModel_.additionalNote, term));
    }

    @Override
    public CriteriaPredicate<ProblemModel> tagsMatch(List<Set<String>> tagGroups) {
        return and(
                tagGroups.stream().map(this::tagsIntersect).collect(toList()));
    }

    private CriteriaPredicate<ProblemModel> tagsIntersect(Set<String> tags) {
        if (tags.isEmpty()) {
            return literalTrue();
        }

        return (cb, cq, root) -> {
            Subquery<ProblemTagModel> sq = cq.subquery(ProblemTagModel.class);
            Root<ProblemTagModel> subRoot = sq.from(ProblemTagModel.class);

            sq.where(
                    cb.equal(subRoot.get(ProblemTagModel_.problemJid), root.get(ProblemModel_.jid)),
                    subRoot.get(ProblemTagModel_.tag).in(tags));
            sq.select(subRoot);

            return cb.exists(sq);
        };
    }

    private CriteriaPredicate<ProblemModel> userIsAuthor(String userJid) {
        return (cb, cq, root) -> cb.equal(root.get(ProblemModel_.createdBy), userJid);
    }

    private CriteriaPredicate<ProblemModel> userIsPartner(String userJid) {
        return (cb, cq, root) -> {
            Subquery<ProblemPartnerModel> sq = cq.subquery(ProblemPartnerModel.class);
            Root<ProblemPartnerModel> subRoot = sq.from(ProblemPartnerModel.class);

            sq.where(
                    cb.equal(subRoot.get(ProblemPartnerModel_.problemJid), root.get(ProblemModel_.jid)),
                    cb.equal(subRoot.get(ProblemPartnerModel_.userJid), userJid));
            sq.select(subRoot);

            return cb.exists(sq);
        };
    }


}
