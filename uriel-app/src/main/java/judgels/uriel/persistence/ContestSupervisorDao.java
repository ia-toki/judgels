package judgels.uriel.persistence;

import java.util.Optional;
import judgels.persistence.Dao;

public interface ContestSupervisorDao extends Dao<ContestSupervisorModel> {
    Optional<ContestSupervisorModel> selectByContestJidAndUserJid(String contestJid, String userJid);
}
