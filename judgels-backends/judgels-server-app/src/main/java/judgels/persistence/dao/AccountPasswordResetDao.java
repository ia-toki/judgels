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
import judgels.persistence.model.AccountPasswordResetModel;
import judgels.persistence.model.AccountPasswordResetModel_;

public class AccountPasswordResetDao extends Dao<AccountPasswordResetModel> {

    private final Clock clock;

    @Inject
    public AccountPasswordResetDao(DaoData data) {
        super(data);
        this.clock = data.getClock();
    }

    public Optional<AccountPasswordResetModel> selectByUserJid(String userJid, Duration expiration) {
        return selectByColumn(expiration, AccountPasswordResetModel_.userJid, userJid);
    }

    public Optional<AccountPasswordResetModel> selectByEmailCode(String emailCode, Duration expiration) {
        return selectByColumn(expiration, AccountPasswordResetModel_.emailCode, emailCode);
    }

    private Optional<AccountPasswordResetModel> selectByColumn(
            Duration expiration,
            SingularAttribute<AccountPasswordResetModel, String> column,
            String val) {

        Instant currentInstant = clock.instant();
        Instant pastInstant = currentInstant.minus(expiration);

        return select()
                .where(columnEq(column, val))
                .where((cb, cq, root) -> cb.isFalse(root.get(AccountPasswordResetModel_.consumed)))
                .where((cb, cq, root) -> cb.between(root.get(Model_.createdAt), cb.literal(pastInstant), cb.literal(currentInstant)))
                .all()
                .stream()
                .findFirst();
    }
}
