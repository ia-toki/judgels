package judgels.uriel.persistence;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import judgels.persistence.Dao;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;

public interface ContestContestantDao extends Dao<ContestContestantModel> {
    Optional<ContestContestantModel> selectByContestJidAndUserJid(String contestJid, String userJid);
    Set<ContestContestantModel> selectAllByContestJidAndUserJids(String contestJid, List<String> userJids);
    Page<ContestContestantModel> selectAllByContestJid(String contestJid, SelectionOptions options);
}
