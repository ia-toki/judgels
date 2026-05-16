package judgels.persistence;

import java.util.List;

public interface ProblemContestDao extends UnmodifiableDao<ProblemContestModel> {
    List<ProblemContestModel> selectAllByProblemJid(String problemJid);
    List<ProblemContestModel> selectAllByContestJid(String contestJid);
}
