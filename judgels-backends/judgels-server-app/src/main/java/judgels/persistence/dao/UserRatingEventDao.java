package judgels.persistence.dao;

import jakarta.inject.Inject;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import judgels.persistence.DaoData;
import judgels.persistence.UnmodifiableDao;
import judgels.persistence.model.UserRatingEventModel;
import judgels.persistence.model.UserRatingEventModel_;

public class UserRatingEventDao extends UnmodifiableDao<UserRatingEventModel> {

    @Inject
    public UserRatingEventDao(DaoData data) {
        super(data);
    }

    public List<UserRatingEventModel> selectAllByTimes(Collection<Instant> times) {
        return select()
                .where(columnIn(UserRatingEventModel_.time, times))
                .all();
    }
}
