package judgels.sandalphon.hibernate;

import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;
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
        return select()
                .where(columnEq(ProblemPartnerModel_.problemJid, problemJid))
                .where(columnEq(ProblemPartnerModel_.userJid, userJid))
                .unique();
    }

    @Override
    public List<ProblemPartnerModel> selectAllByProblemJid(String problemJid) {
        return select().where(columnEq(ProblemPartnerModel_.problemJid, problemJid)).all();
    }
}
