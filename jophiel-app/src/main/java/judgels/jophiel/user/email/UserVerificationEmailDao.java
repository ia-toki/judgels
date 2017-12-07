package judgels.jophiel.user.email;

import java.util.Optional;
import judgels.persistence.Dao;

public interface UserVerificationEmailDao extends Dao<UserVerificationEmailModel> {
    Optional<UserVerificationEmailModel> findByUserJid(String userJid);
    Optional<UserVerificationEmailModel> findByEmailCode(String emailCode);
}
