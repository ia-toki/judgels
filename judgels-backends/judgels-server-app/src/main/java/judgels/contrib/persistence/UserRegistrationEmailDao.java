package judgels.contrib.persistence;

import java.util.Optional;
import judgels.persistence.dao.Dao;

public interface UserRegistrationEmailDao extends Dao<UserRegistrationEmailModel> {
    Optional<UserRegistrationEmailModel> selectByUserJid(String userJid);
    Optional<UserRegistrationEmailModel> selectByEmailCode(String emailCode);
}
