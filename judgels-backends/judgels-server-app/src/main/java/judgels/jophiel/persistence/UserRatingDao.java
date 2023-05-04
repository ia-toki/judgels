package judgels.jophiel.persistence;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import judgels.persistence.UnmodifiableDao;
import judgels.persistence.api.Page;

public interface UserRatingDao extends UnmodifiableDao<UserRatingModel> {
    List<UserRatingModel> selectAllByTimeAndUserJids(Instant time, Set<String> userJids);
    Page<UserRatingModel> selectTopPagedByTime(Instant time, int pageNumber, int pageSize);
    List<UserRatingModel> selectAllByUserJid(String userJid);
}
