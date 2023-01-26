package judgels.sandalphon.persistence;

import java.util.List;
import judgels.persistence.UnmodifiableDao;
import judgels.sandalphon.api.problem.ProblemSetterRole;

public interface ProblemSetterDao extends UnmodifiableDao<ProblemSetterModel> {
    List<ProblemSetterModel> selectAllByProblemJid(String problemJid);
    List<ProblemSetterModel> selectAllByProblemJidAndRole(String problemJid, ProblemSetterRole role);
}
