package judgels.jerahmeel.persistence;

import java.util.Optional;
import judgels.persistence.Dao;

public interface StatsUserDao extends Dao<StatsUserModel> {
    Optional<StatsUserModel> selectByUserJid(String userJid);
}
