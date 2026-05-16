package judgels.persistence;

import java.util.Optional;

public interface StatsUserDao extends Dao<StatsUserModel> {
    Optional<StatsUserModel> selectByUserJid(String userJid);
}
