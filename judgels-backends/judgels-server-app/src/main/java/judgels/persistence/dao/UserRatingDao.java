package judgels.persistence.dao;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import judgels.persistence.UnmodifiableDao;
import judgels.persistence.api.Page;
import judgels.persistence.model.UserRatingModel;

public interface UserRatingDao extends UnmodifiableDao<UserRatingModel> {
    List<UserRatingModel> selectAllByTimeAndUserJids(Instant time, Collection<String> userJids);
    Page<UserRatingModel> selectTopPagedByTime(Instant time, int pageNumber, int pageSize);
    List<UserRatingModel> selectAllByUserJid(String userJid);
}
