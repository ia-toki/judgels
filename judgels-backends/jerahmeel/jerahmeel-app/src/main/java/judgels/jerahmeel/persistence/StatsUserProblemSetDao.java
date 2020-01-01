package judgels.jerahmeel.persistence;

import java.util.Optional;
import judgels.persistence.Dao;

public interface StatsUserProblemSetDao extends Dao<StatsUserProblemSetModel> {
    Optional<StatsUserProblemSetModel> selectByUserJidAndProblemSetJid(String userJid, String problemSetJid);
}
