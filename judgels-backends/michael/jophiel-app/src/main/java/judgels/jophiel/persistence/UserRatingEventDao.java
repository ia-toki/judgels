package judgels.jophiel.persistence;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import judgels.persistence.UnmodifiableDao;

public interface UserRatingEventDao extends UnmodifiableDao<UserRatingEventModel> {
    Map<Instant, UserRatingEventModel> selectAllByTimes(Set<Instant> times);
    Optional<UserRatingEventModel> selectLatest();
}
