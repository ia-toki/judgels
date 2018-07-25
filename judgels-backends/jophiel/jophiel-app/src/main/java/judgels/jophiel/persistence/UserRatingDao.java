package judgels.jophiel.persistence;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import judgels.persistence.UnmodifiableDao;

public interface UserRatingDao extends UnmodifiableDao<UserRatingModel> {
    Map<String, UserRatingModel> selectAllByTimeAndUserJids(Instant time, Set<String> userJids);
}
