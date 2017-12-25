package judgels.jophiel.hibernate;

import java.sql.Date;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import judgels.jophiel.user.password.UserForgotPasswordDao;
import judgels.jophiel.user.password.UserForgotPasswordModel;
import judgels.jophiel.user.password.UserForgotPasswordModel_;
import judgels.persistence.ActorProvider;
import judgels.persistence.Model_;
import judgels.persistence.hibernate.HibernateDao;
import org.hibernate.SessionFactory;

public class UserForgotPasswordHibernateDao extends HibernateDao<UserForgotPasswordModel>
        implements UserForgotPasswordDao {

    private final Clock clock;

    public UserForgotPasswordHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
        this.clock = clock;
    }

    @Override
    public Optional<UserForgotPasswordModel> findUnusedByUserJid(String userJid, Duration expiration) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<UserForgotPasswordModel> cq = cb.createQuery(getEntityClass());
        Root<UserForgotPasswordModel> root = cq.from(getEntityClass());

        Instant currentInstant = clock.instant();
        Date currentDate = new Date(currentInstant.toEpochMilli());
        Date pastDate = new Date(currentInstant.minus(expiration).toEpochMilli());

        cq.where(
                cb.equal(root.get(UserForgotPasswordModel_.userJid), userJid),
                cb.isFalse(root.get(UserForgotPasswordModel_.consumed)),
                cb.between(root.get(Model_.createdAt), cb.literal(pastDate), cb.literal(currentDate)));

        return currentSession().createQuery(cq).list()
                .stream()
                .findFirst();
    }

    @Override
    public Optional<UserForgotPasswordModel> findByEmailCode(String emailCode) {
        return selectByUniqueColumn(UserForgotPasswordModel_.emailCode, emailCode)
                .filter(model -> !model.consumed);
    }
}
