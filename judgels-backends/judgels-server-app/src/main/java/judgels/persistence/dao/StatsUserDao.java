package judgels.persistence.dao;

import java.util.Optional;
import judgels.persistence.Dao;
import judgels.persistence.model.StatsUserModel;

public interface StatsUserDao extends Dao<StatsUserModel> {
    Optional<StatsUserModel> selectByUserJid(String userJid);
}
