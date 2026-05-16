package judgels.uriel.persistence;

import java.io.PrintWriter;
import java.util.Optional;
import judgels.api.contest.scoreboard.ContestScoreboardType;
import judgels.persistence.Dao;
import judgels.persistence.QueryBuilder;

public interface ContestScoreboardDao extends Dao<ContestScoreboardModel> {
    QueryBuilder<ContestScoreboardModel> selectByContestJid(String contestJid);
    Optional<ContestScoreboardModel> selectByContestJidAndType(String contestJid, ContestScoreboardType type);
    void dump(PrintWriter output, String contestJid);
}
