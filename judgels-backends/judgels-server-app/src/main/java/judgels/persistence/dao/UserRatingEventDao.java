package judgels.persistence.dao;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import judgels.persistence.UnmodifiableDao;
import judgels.persistence.model.UserRatingEventModel;

public interface UserRatingEventDao extends UnmodifiableDao<UserRatingEventModel> {
    List<UserRatingEventModel> selectAllByTimes(Collection<Instant> times);
}
