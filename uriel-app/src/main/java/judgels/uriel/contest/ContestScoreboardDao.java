package judgels.uriel.contest;

import java.util.Optional;
import judgels.persistence.Dao;

public interface ContestScoreboardDao extends Dao<ContestScoreboardModel> {
    Optional<ContestScoreboardModel> selectByContestJid(String contestJid, boolean isOfficial);
}
