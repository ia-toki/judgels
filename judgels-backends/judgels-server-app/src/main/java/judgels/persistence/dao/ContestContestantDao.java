package judgels.persistence.dao;

import java.io.PrintWriter;
import java.util.Optional;
import judgels.persistence.Dao;
import judgels.persistence.QueryBuilder;
import judgels.persistence.model.ContestContestantModel;

public interface ContestContestantDao extends Dao<ContestContestantModel> {
    ContestContestantQueryBuilder select();
    ContestContestantQueryBuilder selectByContestJid(String contestJid);
    Optional<ContestContestantModel> selectByContestJidAndUserJid(String contestJid, String userJid);
    void dump(PrintWriter output, String contestJid);

    interface ContestContestantQueryBuilder extends QueryBuilder<ContestContestantModel> {
        ContestContestantQueryBuilder whereUserParticipated(String userJid);
    }
}
