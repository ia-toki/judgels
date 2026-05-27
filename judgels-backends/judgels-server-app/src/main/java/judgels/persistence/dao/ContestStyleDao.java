package judgels.persistence.dao;

import java.util.Optional;
import judgels.persistence.Dao;
import judgels.persistence.model.ContestStyleModel;

public interface ContestStyleDao extends Dao<ContestStyleModel> {
    Optional<ContestStyleModel> selectByContestJid(String contestJid);
}
