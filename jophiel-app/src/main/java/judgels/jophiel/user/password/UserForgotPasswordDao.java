package judgels.jophiel.user.password;

import java.time.Duration;
import java.util.Optional;
import judgels.persistence.Dao;

public interface UserForgotPasswordDao extends Dao<UserForgotPasswordModel> {
    Optional<UserForgotPasswordModel> findUnusedByUserJid(String userJid, Duration expiration);
    Optional<UserForgotPasswordModel> findByEmailCode(String emailCode);
}
