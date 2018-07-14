package judgels.uriel.persistence;

import java.util.List;
import judgels.persistence.JudgelsDao;

public interface ContestClarificationDao extends JudgelsDao<ContestClarificationModel> {
    List<ContestClarificationModel> selectAllByContestJidAndUserJid(String contestJid, String userJid);
    long selectCountAnsweredByContestJidAndUserJid(String contestJid, String userJid);
}
