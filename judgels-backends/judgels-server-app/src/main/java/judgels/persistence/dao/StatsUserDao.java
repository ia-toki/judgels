package judgels.persistence.dao;

import jakarta.inject.Inject;
import java.util.Optional;
import judgels.persistence.Dao;
import judgels.persistence.DaoData;
import judgels.persistence.model.StatsUserModel;
import judgels.persistence.model.StatsUserModel_;

public class StatsUserDao extends Dao<StatsUserModel> {
    @Inject
    public StatsUserDao(DaoData data) {
        super(data);
    }

    public Optional<StatsUserModel> selectByUserJid(String userJid) {
        return select().where(columnEq(StatsUserModel_.userJid, userJid)).unique();
    }
}
