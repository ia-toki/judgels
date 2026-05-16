package judgels.persistence;

import java.io.PrintWriter;

public interface ContestLogDao extends UnmodifiableDao<ContestLogModel> {
    ContestLogQueryBuilder selectByContestJid(String contestJid);
    void updateProblemJid(String oldProblemJid, String newProblemJid);
    void dump(PrintWriter output, String contestJid);

    interface ContestLogQueryBuilder extends QueryBuilder<ContestLogModel> {
        ContestLogQueryBuilder whereUserIs(String userJid);
        ContestLogQueryBuilder whereProblemIs(String problemJid);
    }
}
