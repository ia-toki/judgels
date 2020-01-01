package judgels.jerahmeel.persistence;

import java.util.Optional;
import judgels.persistence.Dao;

public interface StatsUserProblemDao extends Dao<StatsUserProblemModel> {
    Optional<StatsUserProblemModel> selectByUserJidAndProblemJid(String userJid, String problemJid);
}
