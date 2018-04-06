package judgels.uriel.persistence;

import judgels.persistence.Dao;

public interface ContestSupervisorDao extends Dao<ContestSupervisorModel> {
    boolean existsByContestJidAndUserJid(String contestJid, String userJid);
}
