package judgels.persistence.dao;

import java.io.PrintWriter;
import judgels.persistence.QueryBuilder;
import judgels.persistence.UnmodifiableDao;
import judgels.persistence.model.ContestLogModel;

public interface ContestLogDao extends UnmodifiableDao<ContestLogModel> {
    ContestLogQueryBuilder selectByContestJid(String contestJid);
    void updateProblemJid(String oldProblemJid, String newProblemJid);
    void dump(PrintWriter output, String contestJid);

    interface ContestLogQueryBuilder extends QueryBuilder<ContestLogModel> {
        ContestLogQueryBuilder whereUserIs(String userJid);
        ContestLogQueryBuilder whereProblemIs(String problemJid);
    }
}
