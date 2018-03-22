package judgels.uriel.contest.scoreboard;

import java.util.Optional;
import judgels.persistence.Dao;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardType;
import judgels.uriel.persistence.ContestScoreboardModel;

public interface ContestScoreboardDao extends Dao<ContestScoreboardModel> {
    Optional<ContestScoreboardModel> selectByContestJidAndType(String contestJid, ContestScoreboardType type);
}
