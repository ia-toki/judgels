package judgels.persistence.dao;

import jakarta.inject.Inject;
import java.util.Optional;
import judgels.persistence.Dao;
import judgels.persistence.DaoData;
import judgels.persistence.model.SettingModel;
import judgels.persistence.model.SettingModel_;

public class SettingDao extends Dao<SettingModel> {
    @Inject
    public SettingDao(DaoData data) {
        super(data);
    }

    public Optional<SettingModel> selectByKey(String key) {
        return select().where(columnEq(SettingModel_.settingKey, key)).unique();
    }
}
