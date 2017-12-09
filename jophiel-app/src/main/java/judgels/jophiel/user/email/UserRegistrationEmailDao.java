package judgels.jophiel.user.email;

import java.util.Optional;
import judgels.persistence.Dao;

public interface UserRegistrationEmailDao extends Dao<UserRegistrationEmailModel> {
    Optional<UserRegistrationEmailModel> findByUserJid(String userJid);
    Optional<UserRegistrationEmailModel> findByEmailCode(String emailCode);
}
