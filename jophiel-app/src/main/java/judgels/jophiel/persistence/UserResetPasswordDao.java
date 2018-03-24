package judgels.jophiel.persistence;

import java.time.Duration;
import java.util.Optional;
import judgels.persistence.Dao;

public interface UserResetPasswordDao extends Dao<UserResetPasswordModel> {
    Optional<UserResetPasswordModel> selectByUserJid(String userJid, Duration expiration);
    Optional<UserResetPasswordModel> selectByEmailCode(String emailCode, Duration expiration);
}
