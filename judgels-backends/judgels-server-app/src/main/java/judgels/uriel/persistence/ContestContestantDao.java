package judgels.uriel.persistence;

import java.util.Optional;
import judgels.persistence.Dao;
import judgels.persistence.QueryBuilder;

public interface ContestContestantDao extends Dao<ContestContestantModel> {
    ContestContestantQueryBuilder select();
    ContestContestantQueryBuilder selectByContestJid(String contestJid);
    Optional<ContestContestantModel> selectByContestJidAndUserJid(String contestJid, String userJid);

    interface ContestContestantQueryBuilder extends QueryBuilder<ContestContestantModel> {
        ContestContestantQueryBuilder whereUserParticipated(String userJid);
    }
}
