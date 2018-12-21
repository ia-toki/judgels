package judgels.jophiel.persistence;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import judgels.persistence.UnmodifiableDao;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;

public interface UserRatingDao extends UnmodifiableDao<UserRatingModel> {
    Map<String, UserRatingModel> selectAllByTimeAndUserJids(Instant time, Set<String> userJids);
    Page<UserRatingModel> selectTopPagedByTime(Instant time, SelectionOptions options);
    List<UserRatingModel> selectAllByUserJid(String userJid);
}
