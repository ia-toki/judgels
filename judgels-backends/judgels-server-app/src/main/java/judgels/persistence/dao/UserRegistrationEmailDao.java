package judgels.persistence.dao;

import java.util.Optional;
import judgels.persistence.Dao;
import judgels.persistence.model.UserRegistrationEmailModel;

public interface UserRegistrationEmailDao extends Dao<UserRegistrationEmailModel> {
    Optional<UserRegistrationEmailModel> selectByUserJid(String userJid);
    Optional<UserRegistrationEmailModel> selectByEmailCode(String emailCode);
}
