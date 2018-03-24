package judgels.uriel.persistence;

import java.util.List;
import java.util.Set;
import judgels.persistence.Dao;
import judgels.persistence.api.Page;

public interface ContestContestantDao extends Dao<ContestContestantModel> {
    Set<ContestContestantModel> selectAllByUserJids(String contestJid, List<String> userJids);
    Page<ContestContestantModel> selectAllByContestJid(String contestJid, int page, int pageSize);
    boolean existsByContestJidAndUserJid(String contestJid, String userJid);
}
