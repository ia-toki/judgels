package judgels.uriel.persistence;

import java.util.Optional;
import judgels.persistence.Dao;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardType;

public interface ContestGroupScoreboardDao extends Dao<ContestGroupScoreboardModel> {
    Optional<ContestGroupScoreboardModel> selectByContestGroupJidAndType(String contestJid, ContestScoreboardType type);
}
