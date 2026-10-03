package judgels.persistence.dao;

import jakarta.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import judgels.persistence.Dao;
import judgels.persistence.DaoData;
import judgels.persistence.model.UserInfoModel;
import judgels.persistence.model.UserInfoModel_;

public class UserInfoDao extends Dao<UserInfoModel> {
    @Inject
    public UserInfoDao(DaoData data) {
        super(data);
    }

    public Optional<UserInfoModel> selectByUserJid(String userJid) {
        return select().where(columnEq(UserInfoModel_.userJid, userJid)).unique();
    }

    public List<UserInfoModel> selectAllByUserJids(Collection<String> userJids) {
        return select().where(columnIn(UserInfoModel_.userJid, userJids)).all();
    }
}
