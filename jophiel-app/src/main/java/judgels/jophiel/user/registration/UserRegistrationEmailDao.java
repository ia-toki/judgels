package judgels.jophiel.user.registration;

import java.util.Optional;
import judgels.jophiel.persistence.UserRegistrationEmailModel;
import judgels.persistence.Dao;

public interface UserRegistrationEmailDao extends Dao<UserRegistrationEmailModel> {
    Optional<UserRegistrationEmailModel> findByUserJid(String userJid);
    Optional<UserRegistrationEmailModel> findByEmailCode(String emailCode);
}
