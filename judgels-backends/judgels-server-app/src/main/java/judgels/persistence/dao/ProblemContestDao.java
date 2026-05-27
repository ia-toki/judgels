package judgels.persistence.dao;

import java.util.List;
import judgels.persistence.model.ProblemContestModel;

public interface ProblemContestDao extends UnmodifiableDao<ProblemContestModel> {
    List<ProblemContestModel> selectAllByProblemJid(String problemJid);
    List<ProblemContestModel> selectAllByContestJid(String contestJid);
}
