package judgels.persistence;

import java.time.Duration;
import java.util.Optional;

public interface UserResetPasswordDao extends Dao<UserResetPasswordModel> {
    Optional<UserResetPasswordModel> selectByUserJid(String userJid, Duration expiration);
    Optional<UserResetPasswordModel> selectByEmailCode(String emailCode, Duration expiration);
}
