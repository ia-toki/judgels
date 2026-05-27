package judgels.persistence.dao;

import java.io.PrintWriter;
import java.util.Optional;
import judgels.api.contest.scoreboard.ContestScoreboardType;
import judgels.persistence.Dao;
import judgels.persistence.QueryBuilder;
import judgels.persistence.model.ContestScoreboardModel;

public interface ContestScoreboardDao extends Dao<ContestScoreboardModel> {
    QueryBuilder<ContestScoreboardModel> selectByContestJid(String contestJid);
    Optional<ContestScoreboardModel> selectByContestJidAndType(String contestJid, ContestScoreboardType type);
    void dump(PrintWriter output, String contestJid);
}
