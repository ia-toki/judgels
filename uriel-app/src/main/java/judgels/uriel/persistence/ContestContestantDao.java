package judgels.uriel.persistence;

import java.util.List;
import java.util.Set;
import judgels.persistence.Dao;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;

public interface ContestContestantDao extends Dao<ContestContestantModel> {
    Set<ContestContestantModel> selectAllByContestJidAndUserJids(String contestJid, List<String> userJids);
    Page<ContestContestantModel> selectAllByContestJid(String contestJid, SelectionOptions options);
    boolean existsByContestJidAndUserJid(String contestJid, String userJid);
}
