package judgels.uriel.persistence;

import java.util.Optional;
import judgels.persistence.Dao;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardType;

public interface ContestScoreboardDao extends Dao<ContestScoreboardModel> {
    Optional<ContestScoreboardModel> selectByContestJidAndType(String contestJid, ContestScoreboardType type);
}
