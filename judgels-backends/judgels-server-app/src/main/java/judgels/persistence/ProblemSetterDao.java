package judgels.persistence;

import java.util.List;
import judgels.api.problem.ProblemSetterRole;

public interface ProblemSetterDao extends UnmodifiableDao<ProblemSetterModel> {
    List<ProblemSetterModel> selectAllByProblemJid(String problemJid);
    List<ProblemSetterModel> selectAllByProblemJidAndRole(String problemJid, ProblemSetterRole role);
}
