package judgels.persistence.dao;

import java.util.List;
import java.util.Optional;
import judgels.persistence.Dao;
import judgels.persistence.model.ProblemPartnerModel;

public interface ProblemPartnerDao extends Dao<ProblemPartnerModel> {
    Optional<ProblemPartnerModel> selectByProblemJidAndUserJid(String problemJid, String userJid);
    List<ProblemPartnerModel> selectAllByProblemJid(String problemJid);
}
