package judgels.jophiel.hibernate;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.jophiel.persistence.UserRatingEventDao;
import judgels.jophiel.persistence.UserRatingEventModel;
import judgels.jophiel.persistence.UserRatingEventModel_;
import judgels.persistence.FilterOptions;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.UnmodifiableHibernateDao;

@Singleton
public class UserRatingEventHibernateDao extends UnmodifiableHibernateDao<UserRatingEventModel>
        implements UserRatingEventDao {

    @Inject
    public UserRatingEventHibernateDao(HibernateDaoData data) {
        super(data);
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
