package judgels.jophiel.hibernate;

import java.sql.Date;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;
import judgels.jophiel.persistence.UserResetPasswordDao;
import judgels.jophiel.persistence.UserResetPasswordModel;
import judgels.jophiel.persistence.UserResetPasswordModel_;
import judgels.persistence.ActorProvider;
import judgels.persistence.Model_;
import judgels.persistence.hibernate.HibernateDao;
import org.hibernate.SessionFactory;

@Singleton
public class UserResetPasswordHibernateDao extends HibernateDao<UserResetPasswordModel>
        implements UserResetPasswordDao {

    private final Clock clock;

    @Inject
    public UserResetPasswordHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
        this.clock = clock;
    }

    @Override
    public Optional<UserResetPasswordModel> selectByUserJid(String userJid, Duration expiration) {
        return select(expiration, UserResetPasswordModel_.userJid, userJid);
    }

    @Override
    public Optional<UserResetPasswordModel> selectByEmailCode(String emailCode, Duration expiration) {
        return select(expiration, UserResetPasswordModel_.emailCode, emailCode);
    }

    private Optional<UserResetPasswordModel> select(
            Duration expiration,
            SingularAttribute<UserResetPasswordModel, ?> attr,
            Object val) {

        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<UserResetPasswordModel> cq = cb.createQuery(UserResetPasswordModel.class);
        Root<UserResetPasswordModel> root = cq.from(UserResetPasswordModel.class);

        Instant currentInstant = clock.instant();
        Date currentDate = new Date(currentInstant.toEpochMilli());
        Date pastDate = new Date(currentInstant.minus(expiration).toEpochMilli());

        cq.where(
                cb.equal(root.get(attr), val),
                cb.isFalse(root.get(UserResetPasswordModel_.consumed)),
                cb.between(root.get(Model_.createdAt), cb.literal(pastDate), cb.literal(currentDate)));

        return currentSession().createQuery(cq).list()
                .stream()
                .findFirst();
    }
}
