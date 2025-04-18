package judgels.uriel.persistence;

import java.io.PrintWriter;
import java.util.Optional;
import judgels.persistence.JudgelsDao;
import judgels.persistence.QueryBuilder;

public interface ContestClarificationDao extends JudgelsDao<ContestClarificationModel> {
    ContestClarificationQueryBuilder selectByContestJid(String contestJid);
    Optional<ContestClarificationModel> selectByContestJidAndClarificationJid(String contestJid, String clarificationJid);
    void updateTopicJid(String oldTopicJid, String newTopicJid);
    void dump(PrintWriter output, String contestJid);

    interface ContestClarificationQueryBuilder extends QueryBuilder<ContestClarificationModel> {
        ContestClarificationQueryBuilder whereUserIsAsker(String userJid);
        ContestClarificationQueryBuilder whereStatusIs(String status);
    }
}
