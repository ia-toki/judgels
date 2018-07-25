package judgels.jophiel.hibernate;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.jophiel.persistence.UserRatingEventDao;
import judgels.jophiel.persistence.UserRatingEventModel;
import judgels.jophiel.persistence.UserRatingEventModel_;
import judgels.persistence.ActorProvider;
import judgels.persistence.FilterOptions;
import judgels.persistence.api.OrderDir;
import judgels.persistence.api.SelectionOptions;
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
    public Optional<UserRatingEventModel> selectLatestBefore(Instant time) {
        return selectAll(new FilterOptions.Builder<UserRatingEventModel>()
                .addCustomPredicates((cb, cq, root) -> cb.lessThan(root.get(UserRatingEventModel_.time), time))
                .build(), new SelectionOptions.Builder()
                .from(SelectionOptions.DEFAULT_ALL)
                .orderBy("time")
                .orderDir(OrderDir.DESC)
                .build()).stream()
                .findFirst();
    }
}
