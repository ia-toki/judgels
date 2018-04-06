package judgels.uriel.persistence;

import judgels.persistence.Dao;

public interface ContestManagerDao extends Dao<ContestManagerModel> {
    boolean existsByContestJidAndUserJid(String contestJid, String userJid);
}
