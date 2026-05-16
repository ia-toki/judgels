package judgels.persistence;

import java.util.Optional;

public interface ContestStyleDao extends Dao<ContestStyleModel> {
    Optional<ContestStyleModel> selectByContestJid(String contestJid);
}
