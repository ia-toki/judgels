package judgels.uriel.contest.supervisor;

import java.util.Set;
import judgels.persistence.Dao;
import judgels.persistence.api.Page;

public interface ContestSupervisorDao extends Dao<ContestSupervisorModel> {
    Set<ContestSupervisorModel> selectAllByUserJids(String contestJid, Set<String> userJids);
    Set<ContestSupervisorModel> insertAll(String contestJid, Set<ContestSupervisorModel> supervisors);
    Page<ContestSupervisorModel> selectAllByContestJid(String contestJid, int page, int pageSize);
    boolean existsByContestJidAndUserJid(String contestJid, String userJid);
}
