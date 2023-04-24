package judgels.sandalphon.hibernate;

import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import judgels.persistence.CustomPredicateFilter;
import judgels.persistence.FilterOptions;
import judgels.persistence.hibernate.HibernateDao;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.sandalphon.persistence.ProblemModel;
import judgels.sandalphon.persistence.ProblemModel_;
import judgels.sandalphon.persistence.ProblemPartnerDao;
import judgels.sandalphon.persistence.ProblemPartnerModel;
import judgels.sandalphon.persistence.ProblemPartnerModel_;

public final class ProblemPartnerHibernateDao extends HibernateDao<ProblemPartnerModel> implements ProblemPartnerDao {

    @Inject
    public ProblemPartnerHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public Optional<ProblemPartnerModel> selectByProblemJidAndUserJid(String problemJid, String userJid) {
        return selectByFilter(new FilterOptions.Builder<ProblemPartnerModel>()
                .putColumnsEq(ProblemPartnerModel_.problemJid, problemJid)
                .putColumnsEq(ProblemPartnerModel_.userJid, userJid)
                .build());
    }

    @Override
    public List<ProblemPartnerModel> selectAllByProblemJid(String problemJid) {
        return selectAll(new FilterOptions.Builder<ProblemPartnerModel>()
                .putColumnsEq(ProblemPartnerModel_.problemJid, problemJid)
                .build());
    }

    static CustomPredicateFilter<ProblemModel> hasPartner(String userJid) {
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
