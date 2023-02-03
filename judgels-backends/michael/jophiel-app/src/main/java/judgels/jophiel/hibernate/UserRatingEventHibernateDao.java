package judgels.jophiel.hibernate;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.jophiel.persistence.UserRatingEventDao;
import judgels.jophiel.persistence.UserRatingEventModel;
import judgels.jophiel.persistence.UserRatingEventModel_;
import judgels.persistence.FilterOptions;
import judgels.persistence.api.SelectionOptions;
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

    @Override
    public Optional<UserRatingEventModel> selectLatest() {
        List<UserRatingEventModel> models = selectAll(new SelectionOptions.Builder()
                .pageSize(1)
                .build());
        if (models.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(models.get(0));
    }
}
