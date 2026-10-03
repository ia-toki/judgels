package judgels.persistence.dao;

import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;
import judgels.persistence.Dao;
import judgels.persistence.DaoData;
import judgels.persistence.model.ProblemPartnerModel;
import judgels.persistence.model.ProblemPartnerModel_;

public final class ProblemPartnerDao extends Dao<ProblemPartnerModel> {
    @Inject
    public ProblemPartnerDao(DaoData data) {
        super(data);
    }

    public Optional<ProblemPartnerModel> selectByProblemJidAndUserJid(String problemJid, String userJid) {
        return select()
                .where(columnEq(ProblemPartnerModel_.problemJid, problemJid))
                .where(columnEq(ProblemPartnerModel_.userJid, userJid))
                .unique();
    }

    public List<ProblemPartnerModel> selectAllByProblemJid(String problemJid) {
        return select().where(columnEq(ProblemPartnerModel_.problemJid, problemJid)).all();
    }
}
