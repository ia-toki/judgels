package judgels.persistence.dao;

import jakarta.inject.Inject;
import jakarta.persistence.metamodel.SingularAttribute;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import judgels.persistence.Dao;
import judgels.persistence.DaoData;
import judgels.persistence.Model_;
import judgels.persistence.model.UserResetPasswordModel;
import judgels.persistence.model.UserResetPasswordModel_;

public class UserResetPasswordDao extends Dao<UserResetPasswordModel> {

    private final Clock clock;

    @Inject
    public UserResetPasswordDao(DaoData data) {
        super(data);
        this.clock = data.getClock();
    }

    public Optional<UserResetPasswordModel> selectByUserJid(String userJid, Duration expiration) {
        return selectByColumn(expiration, UserResetPasswordModel_.userJid, userJid);
    }

    public Optional<UserResetPasswordModel> selectByEmailCode(String emailCode, Duration expiration) {
        return selectByColumn(expiration, UserResetPasswordModel_.emailCode, emailCode);
    }

    private Optional<UserResetPasswordModel> selectByColumn(
            Duration expiration,
            SingularAttribute<UserResetPasswordModel, String> column,
            String val) {

        Instant currentInstant = clock.instant();
        Instant pastInstant = currentInstant.minus(expiration);

        return select()
                .where(columnEq(column, val))
                .where((cb, cq, root) -> cb.isFalse(root.get(UserResetPasswordModel_.consumed)))
                .where((cb, cq, root) -> cb.between(root.get(Model_.createdAt), cb.literal(pastInstant), cb.literal(currentInstant)))
                .all()
                .stream()
                .findFirst();
    }
}
