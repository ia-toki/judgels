package judgels.uriel.contest.manager;

import java.util.Set;
import judgels.persistence.Dao;
import judgels.persistence.api.Page;

public interface ContestManagerDao extends Dao<ContestManagerModel> {
    Set<ContestManagerModel> selectAllByUserJids(String contestJid, Set<String> userJids);
    Set<ContestManagerModel> insertAll(String contestJid, Set<ContestManagerModel> managers);
    Page<ContestManagerModel> selectAllByContestJid(String contestJid, int page, int pageSize);
    boolean existsByContestJidAndUserJid(String contestJid, String userJid);
}
