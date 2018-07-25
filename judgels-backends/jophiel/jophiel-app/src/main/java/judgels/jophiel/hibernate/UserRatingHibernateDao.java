package judgels.jophiel.hibernate;

import java.time.Clock;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.jophiel.persistence.UserRatingDao;
import judgels.jophiel.persistence.UserRatingModel;
import judgels.jophiel.persistence.UserRatingModel_;
import judgels.persistence.ActorProvider;
import judgels.persistence.FilterOptions;
import judgels.persistence.hibernate.UnmodifiableHibernateDao;
import org.hibernate.SessionFactory;

@Singleton
public class UserRatingHibernateDao extends UnmodifiableHibernateDao<UserRatingModel> implements UserRatingDao {
    @Inject
    public UserRatingHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
    }

    @Override
    public Map<String, UserRatingModel> selectAllByTimeAndUserJids(Instant time, Set<String> userJids) {
        return selectAll(new FilterOptions.Builder<UserRatingModel>()
                .putColumnsEq(UserRatingModel_.time, time)
                .putColumnsIn(UserRatingModel_.userJid, userJids)
                .build())
                .stream()
                .collect(Collectors.toMap(m -> m.userJid, m -> m));
    }
}
