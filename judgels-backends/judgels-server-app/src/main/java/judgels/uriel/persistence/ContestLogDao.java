package judgels.uriel.persistence;

import judgels.persistence.QueryBuilder;
import judgels.persistence.UnmodifiableDao;

public interface ContestLogDao extends UnmodifiableDao<ContestLogModel> {
    ContestLogQueryBuilder selectByContestJid(String contestJid);

    interface ContestLogQueryBuilder extends QueryBuilder<ContestLogModel> {
        ContestLogQueryBuilder whereUserIs(String userJid);
        ContestLogQueryBuilder whereProblemIs(String problemJid);
    }
}
