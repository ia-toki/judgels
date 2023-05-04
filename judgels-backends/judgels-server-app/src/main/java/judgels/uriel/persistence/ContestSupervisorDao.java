package judgels.uriel.persistence;

import java.util.Optional;
import judgels.persistence.Dao;
import judgels.persistence.QueryBuilder;

public interface ContestSupervisorDao extends Dao<ContestSupervisorModel> {
    QueryBuilder<ContestSupervisorModel> selectByContestJid(String contestJid);
    Optional<ContestSupervisorModel> selectByContestJidAndUserJid(String contestJid, String userJid);
}
