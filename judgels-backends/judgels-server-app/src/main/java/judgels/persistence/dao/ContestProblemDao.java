package judgels.persistence.dao;

import java.io.PrintWriter;
import java.util.Optional;
import judgels.persistence.QueryBuilder;
import judgels.persistence.api.OrderDir;
import judgels.persistence.model.ContestProblemModel;

public interface ContestProblemDao extends Dao<ContestProblemModel> {
    ContestProblemQueryBuilder selectByContestJid(String contestJid);
    Optional<ContestProblemModel> selectByContestJidAndProblemJid(String contestJid, String problemJid);
    Optional<ContestProblemModel> selectByContestJidAndProblemAlias(String contestJid, String problemAlias);
    void updateProblemJid(String oldProblemJid, String newProblemJid);
    void dump(PrintWriter output, String contestJid);

    interface ContestProblemQueryBuilder extends QueryBuilder<ContestProblemModel> {
        ContestProblemQueryBuilder orderBy(String column, OrderDir dir);
        ContestProblemQueryBuilder whereStatusIs(String status);
    }
}
