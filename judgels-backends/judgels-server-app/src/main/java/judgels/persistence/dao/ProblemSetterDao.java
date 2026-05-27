package judgels.persistence.dao;

import java.util.List;
import judgels.api.problem.ProblemSetterRole;
import judgels.persistence.UnmodifiableDao;
import judgels.persistence.model.ProblemSetterModel;

public interface ProblemSetterDao extends UnmodifiableDao<ProblemSetterModel> {
    List<ProblemSetterModel> selectAllByProblemJid(String problemJid);
    List<ProblemSetterModel> selectAllByProblemJidAndRole(String problemJid, ProblemSetterRole role);
}
