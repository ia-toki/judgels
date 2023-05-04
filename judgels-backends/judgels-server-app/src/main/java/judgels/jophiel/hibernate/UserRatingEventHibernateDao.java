package judgels.jophiel.hibernate;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import judgels.jophiel.persistence.UserRatingEventDao;
import judgels.jophiel.persistence.UserRatingEventModel;
import judgels.jophiel.persistence.UserRatingEventModel_;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.UnmodifiableHibernateDao;

public class UserRatingEventHibernateDao extends UnmodifiableHibernateDao<UserRatingEventModel>
        implements UserRatingEventDao {

    @Inject
    public UserRatingEventHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public List<UserRatingEventModel> selectAllByTimes(Set<Instant> times) {
        return select()
                .where(columnIn(UserRatingEventModel_.time, times))
                .all();
    }
}
