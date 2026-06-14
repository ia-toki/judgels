package judgels.persistence.dao;

import java.util.Optional;
import judgels.persistence.Dao;
import judgels.persistence.model.SettingModel;

public interface SettingDao extends Dao<SettingModel> {
    Optional<SettingModel> selectByKey(String key);
}
