package judgels.jophiel.hibernate;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import javax.inject.Inject;
import javax.persistence.metamodel.SingularAttribute;
import judgels.jophiel.persistence.UserResetPasswordDao;
import judgels.jophiel.persistence.UserResetPasswordModel;
import judgels.jophiel.persistence.UserResetPasswordModel_;
import judgels.persistence.Model_;
import judgels.persistence.hibernate.HibernateDao;
import judgels.persistence.hibernate.HibernateDaoData;

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
