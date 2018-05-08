package judgels.uriel.persistence;

import java.util.Optional;
import judgels.persistence.Dao;

public interface ContestStyleDao extends Dao<ContestStyleModel> {
    Optional<ContestStyleModel> selectByContestJid(String contestJid);
}
