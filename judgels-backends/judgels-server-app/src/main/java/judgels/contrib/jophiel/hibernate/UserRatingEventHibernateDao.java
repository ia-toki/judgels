package judgels.contrib.jophiel.hibernate;

import jakarta.inject.Inject;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import judgels.contrib.jophiel.persistence.UserRatingEventDao;
import judgels.contrib.jophiel.persistence.UserRatingEventModel;
import judgels.contrib.jophiel.persistence.UserRatingEventModel_;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.UnmodifiableHibernateDao;

public class UserRatingEventHibernateDao extends UnmodifiableHibernateDao<UserRatingEventModel>
        implements UserRatingEventDao {

    @Inject
    public UserRatingEventHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public List<UserRatingEventModel> selectAllByTimes(Collection<Instant> times) {
        return select()
                .where(columnIn(UserRatingEventModel_.time, times))
                .all();
    }
}
