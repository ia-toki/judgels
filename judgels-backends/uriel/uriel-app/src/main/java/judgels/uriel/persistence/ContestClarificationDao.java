package judgels.uriel.persistence;

import java.util.List;
import java.util.Optional;
import judgels.persistence.JudgelsDao;

public interface ContestClarificationDao extends JudgelsDao<ContestClarificationModel> {
    List<ContestClarificationModel> selectAllByContestJidAndUserJid(String contestJid, String userJid);
    List<ContestClarificationModel> selectAllByContestJid(String contestJid);
    long selectCountAnsweredByContestJidAndUserJid(String contestJid, String userJid);
    long selectCountAskedByContestJid(String contestJid);
    Optional<ContestClarificationModel> selectByContestJid(String contestJid);
}
