package judgels.persistence.dao;

import java.util.Optional;
import judgels.persistence.model.UserRoleModel;

public interface UserRoleDao extends Dao<UserRoleModel> {
    Optional<UserRoleModel> selectByUserJid(String userJid);
}
