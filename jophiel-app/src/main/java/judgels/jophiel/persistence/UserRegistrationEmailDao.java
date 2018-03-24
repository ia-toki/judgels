package judgels.jophiel.persistence;

import java.util.Optional;
import judgels.persistence.Dao;

public interface UserRegistrationEmailDao extends Dao<UserRegistrationEmailModel> {
    Optional<UserRegistrationEmailModel> selectByUserJid(String userJid);
    Optional<UserRegistrationEmailModel> selectByEmailCode(String emailCode);
}
