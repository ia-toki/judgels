package judgels.jerahmeel.persistence;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import judgels.persistence.Dao;

public interface StatsUserProblemSetDao extends Dao<StatsUserProblemSetModel> {
    Optional<StatsUserProblemSetModel> selectByUserJidAndProblemSetJid(String userJid, String problemSetJid);
    List<StatsUserProblemSetModel> selectAllByUserJidAndProblemSetJids(String userJid, Set<String> problemSetJids);
}
