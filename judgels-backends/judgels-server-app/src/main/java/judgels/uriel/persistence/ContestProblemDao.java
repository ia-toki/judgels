package judgels.uriel.persistence;

import java.io.PrintWriter;
import java.util.Optional;
import judgels.persistence.Dao;
import judgels.persistence.QueryBuilder;
import judgels.persistence.api.OrderDir;

public interface ContestProblemDao extends Dao<ContestProblemModel> {
    ContestProblemQueryBuilder selectByContestJid(String contestJid);
    Optional<ContestProblemModel> selectByContestJidAndProblemJid(String contestJid, String problemJid);
    Optional<ContestProblemModel> selectByContestJidAndProblemAlias(String contestJid, String problemAlias);
    void dump(PrintWriter output, String contestJid);

    interface ContestProblemQueryBuilder extends QueryBuilder<ContestProblemModel> {
        ContestProblemQueryBuilder orderBy(String column, OrderDir dir);
        ContestProblemQueryBuilder whereStatusIs(String status);
    }
}
