package judgels.jophiel.hibernate;

import java.time.Clock;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.jophiel.persistence.UserRatingEventDao;
import judgels.jophiel.persistence.UserRatingEventModel;
import judgels.jophiel.persistence.UserRatingEventModel_;
import judgels.persistence.ActorProvider;
import judgels.persistence.FilterOptions;
import judgels.persistence.hibernate.UnmodifiableHibernateDao;
import org.hibernate.SessionFactory;

@Singleton
public class UserRatingEventHibernateDao extends UnmodifiableHibernateDao<UserRatingEventModel>
        implements UserRatingEventDao {

    @Inject
    public UserRatingEventHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
    }

    @Override
    public Map<Instant, UserRatingEventModel> selectAllByTimes(Set<Instant> times) {
        return selectAll(new FilterOptions.Builder<UserRatingEventModel>()
                .putColumnsIn(UserRatingEventModel_.time, times)
                .build())
                .stream()
                .collect(Collectors.toMap(
                        e -> e.time,
                        e -> e));
    }
}
