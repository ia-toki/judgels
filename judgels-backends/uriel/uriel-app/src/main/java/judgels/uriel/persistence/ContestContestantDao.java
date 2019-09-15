package judgels.uriel.persistence;

import java.util.Optional;
import java.util.Set;
import judgels.persistence.Dao;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;

public interface ContestContestantDao extends Dao<ContestContestantModel> {
    Optional<ContestContestantModel> selectByContestJidAndUserJid(String contestJid, String userJid);
    long selectCountApprovedByContestJid(String contestJid);
    Page<ContestContestantModel> selectPagedByContestJid(String contestJid, SelectionOptions options);
    Set<ContestContestantModel> selectAllByContestJid(String contestJid, SelectionOptions options);
    Set<ContestContestantModel> selectAllApprovedByContestJid(String contestJid);
    Set<ContestContestantModel> selectAllParticipated(String userJid);
}
