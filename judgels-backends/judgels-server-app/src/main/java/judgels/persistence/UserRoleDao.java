package judgels.persistence;

import java.util.Optional;

public interface UserRoleDao extends Dao<UserRoleModel> {
    Optional<UserRoleModel> selectByUserJid(String userJid);
}
