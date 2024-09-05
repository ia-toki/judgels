package judgels.uriel.persistence;

import java.io.PrintWriter;
import java.util.Optional;
import judgels.persistence.Dao;
import judgels.persistence.QueryBuilder;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardType;

public interface ContestScoreboardDao extends Dao<ContestScoreboardModel> {
    QueryBuilder<ContestScoreboardModel> selectByContestJid(String contestJid);
    Optional<ContestScoreboardModel> selectByContestJidAndType(String contestJid, ContestScoreboardType type);
    void dump(PrintWriter output, String contestJid);
}
