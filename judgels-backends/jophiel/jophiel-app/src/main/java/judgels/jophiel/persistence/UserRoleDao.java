package judgels.jophiel.persistence;

import java.util.Optional;
import judgels.persistence.Dao;

public interface UserRoleDao extends Dao<UserRoleModel> {
    Optional<UserRoleModel> selectByUserJid(String userJid);
}
