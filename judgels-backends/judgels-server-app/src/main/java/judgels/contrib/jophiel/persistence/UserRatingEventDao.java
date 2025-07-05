package judgels.contrib.jophiel.persistence;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import judgels.persistence.UnmodifiableDao;

public interface UserRatingEventDao extends UnmodifiableDao<UserRatingEventModel> {
    List<UserRatingEventModel> selectAllByTimes(Collection<Instant> times);
}
