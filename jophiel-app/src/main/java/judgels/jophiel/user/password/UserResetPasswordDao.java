package judgels.jophiel.user.password;

import java.time.Duration;
import java.util.Optional;
import judgels.persistence.Dao;

public interface UserResetPasswordDao extends Dao<UserResetPasswordModel> {
    Optional<UserResetPasswordModel> findByUserJid(String userJid, Duration expiration);
    Optional<UserResetPasswordModel> findByEmailCode(String emailCode, Duration expiration);
}
