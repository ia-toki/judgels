package judgels.persistence;

import java.util.List;
import java.util.Optional;

public interface ProblemPartnerDao extends Dao<ProblemPartnerModel> {
    Optional<ProblemPartnerModel> selectByProblemJidAndUserJid(String problemJid, String userJid);
    List<ProblemPartnerModel> selectAllByProblemJid(String problemJid);
}
