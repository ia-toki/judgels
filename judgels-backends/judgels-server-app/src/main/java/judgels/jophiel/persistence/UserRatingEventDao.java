package judgels.jophiel.persistence;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import judgels.persistence.UnmodifiableDao;

public interface UserRatingEventDao extends UnmodifiableDao<UserRatingEventModel> {
    List<UserRatingEventModel> selectAllByTimes(Set<Instant> times);
}
