package judgels.jophiel.persistence;

import java.time.Instant;
import java.util.Optional;
import judgels.persistence.UnmodifiableDao;

public interface UserRatingEventDao extends UnmodifiableDao<UserRatingEventModel> {
    Optional<UserRatingEventModel> selectLatestBefore(Instant time);
}
