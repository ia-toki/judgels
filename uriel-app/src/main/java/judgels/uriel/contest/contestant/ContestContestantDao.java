package judgels.uriel.contest.contestant;

import java.util.Set;
import judgels.persistence.Dao;
import judgels.persistence.api.Page;
import judgels.uriel.persistence.ContestContestantModel;

public interface ContestContestantDao extends Dao<ContestContestantModel> {
    Set<ContestContestantModel> selectAllByUserJids(String contestJid, Set<String> userJids);
    Set<ContestContestantModel> insertAll(String contestJid, Set<ContestContestantModel> contestantModels);
    Page<ContestContestantModel> selectAllByContestJid(String contestJid, int page, int pageSize);
    boolean existsByContestJidAndUserJid(String contestJid, String userJid);
}
