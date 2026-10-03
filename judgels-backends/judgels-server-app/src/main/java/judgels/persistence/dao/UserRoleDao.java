package judgels.persistence.dao;

import jakarta.inject.Inject;
import java.util.Optional;
import judgels.persistence.Dao;
import judgels.persistence.DaoData;
import judgels.persistence.model.UserRoleModel;
import judgels.persistence.model.UserRoleModel_;

public class UserRoleDao extends Dao<UserRoleModel> {
    @Inject
    public UserRoleDao(DaoData data) {
        super(data);
    }

    public Optional<UserRoleModel> selectByUserJid(String userJid) {
        return select().where(columnEq(UserRoleModel_.userJid, userJid)).unique();
    }
}
