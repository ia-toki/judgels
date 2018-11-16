package judgels.uriel.persistence;

import java.util.Optional;
import java.util.Set;
import judgels.persistence.Dao;

public interface ContestContestantDao extends Dao<ContestContestantModel> {
    Optional<ContestContestantModel> selectByContestJidAndUserJid(String contestJid, String userJid);
    long selectCountApprovedByContestJid(String contestJid);
    Set<ContestContestantModel> selectAllByContestJid(String contestJid);
    Set<ContestContestantModel> selectAllApprovedByContestJid(String contestJid);
}
