package judgels.uriel.persistence;

import java.util.Optional;
import java.util.Set;
import judgels.persistence.Dao;

public interface ContestContestantDao extends Dao<ContestContestantModel> {
    Optional<ContestContestantModel> selectByContestJidAndUserJid(String contestJid, String userJid);
    Set<ContestContestantModel> selectAllByContestJidAndUserJids(String contestJid, Set<String> userJids);
    long selectCountByContestJid(String contestJid);
    Set<ContestContestantModel> selectAllByContestJid(String contestJid);
}
