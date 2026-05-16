package judgels.sandalphon.persistence;

import java.util.List;
import judgels.api.problem.ProblemSetterRole;
import judgels.persistence.UnmodifiableDao;

public interface ProblemSetterDao extends UnmodifiableDao<ProblemSetterModel> {
    List<ProblemSetterModel> selectAllByProblemJid(String problemJid);
    List<ProblemSetterModel> selectAllByProblemJidAndRole(String problemJid, ProblemSetterRole role);
}
