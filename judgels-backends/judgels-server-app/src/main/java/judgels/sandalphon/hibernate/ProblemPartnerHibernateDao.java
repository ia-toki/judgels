package judgels.sandalphon.hibernate;

import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import judgels.persistence.FilterOptions;
import judgels.persistence.hibernate.HibernateDao;
import judgels.persistence.hibernate.HibernateDaoData;
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
}
