package judgels.persistence;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

public interface UserRatingEventDao extends UnmodifiableDao<UserRatingEventModel> {
    List<UserRatingEventModel> selectAllByTimes(Collection<Instant> times);
}
