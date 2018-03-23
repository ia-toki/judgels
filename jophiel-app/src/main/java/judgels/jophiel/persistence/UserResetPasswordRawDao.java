package judgels.jophiel.persistence;

import java.time.Duration;
import java.util.Optional;

public interface UserResetPasswordRawDao {
    Optional<UserResetPasswordModel> selectByUserJid(String userJid, Duration expiration);
    Optional<UserResetPasswordModel> selectByEmailCode(String emailCode, Duration expiration);
}
