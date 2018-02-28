package judgels.uriel.contest.contestant;

import java.util.Set;
import judgels.persistence.Dao;
import judgels.persistence.api.Page;

public interface ContestContestantDao extends Dao<ContestContestantModel> {
    Set<ContestContestantModel> selectAllByUserJids(String contestJid, Set<String> userJids);
    Set<ContestContestantModel> insertAll(String contestJid, Set<ContestContestantModel> userJids);
    Page<ContestContestantModel> selectAllByContestJid(String contestId, int page, int pageSize);
}
