package judgels.sandalphon.persistence;

import java.util.List;
import judgels.persistence.Dao;

public interface ProblemPartnerDao extends Dao<ProblemPartnerModel> {

    boolean existsByProblemJidAndPartnerJid(String problemJid, String partnerJid);

    ProblemPartnerModel findByProblemJidAndPartnerJid(String problemJid, String partnerJid);

    List<String> getProblemJidsByPartnerJid(String partnerJid);
}
