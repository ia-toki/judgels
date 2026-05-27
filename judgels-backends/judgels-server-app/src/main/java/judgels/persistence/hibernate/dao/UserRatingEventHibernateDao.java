package judgels.persistence.hibernate.dao;

import jakarta.inject.Inject;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import judgels.persistence.dao.UserRatingEventDao;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.UnmodifiableHibernateDao;
import judgels.persistence.model.UserRatingEventModel;
import judgels.persistence.model.UserRatingEventModel_;

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
