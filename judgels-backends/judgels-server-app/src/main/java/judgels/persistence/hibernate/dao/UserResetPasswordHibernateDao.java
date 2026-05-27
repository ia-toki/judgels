package judgels.persistence.hibernate.dao;

import jakarta.inject.Inject;
import jakarta.persistence.metamodel.SingularAttribute;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import judgels.persistence.Model_;
import judgels.persistence.dao.UserResetPasswordDao;
import judgels.persistence.hibernate.HibernateDao;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.model.UserResetPasswordModel;
import judgels.persistence.model.UserResetPasswordModel_;

public class UserResetPasswordHibernateDao extends HibernateDao<UserResetPasswordModel>
        implements UserResetPasswordDao {

    private final Clock clock;

    @Inject
    public UserResetPasswordHibernateDao(HibernateDaoData data) {
        super(data);
        this.clock = data.getClock();
    }

    @Override
    public Optional<UserResetPasswordModel> selectByUserJid(String userJid, Duration expiration) {
        return selectByColumn(expiration, UserResetPasswordModel_.userJid, userJid);
    }

    @Override
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
