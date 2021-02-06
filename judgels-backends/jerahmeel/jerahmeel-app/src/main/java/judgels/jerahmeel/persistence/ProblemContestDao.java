package judgels.jerahmeel.persistence;

import java.util.List;
import judgels.persistence.UnmodifiableDao;

public interface ProblemContestDao extends UnmodifiableDao<ProblemContestModel> {
    List<ProblemContestModel> selectAllByProblemJid(String problemJid);
    List<ProblemContestModel> selectAllByContestJid(String contestJid);
}
