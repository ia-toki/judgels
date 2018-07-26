package judgels.jophiel.user.rating;

import com.google.common.collect.ImmutableMap;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.jophiel.persistence.UserRatingDao;
import judgels.jophiel.persistence.UserRatingEventDao;
import judgels.jophiel.persistence.UserRatingEventModel;

@Singleton
public class UserRatingStore {
    private final UserRatingDao ratingDao;
    private final UserRatingEventDao ratingEventDao;

    @Inject
    public UserRatingStore(UserRatingDao ratingDao, UserRatingEventDao ratingEventDao) {
        this.ratingDao = ratingDao;
        this.ratingEventDao = ratingEventDao;
    }

    public Map<String, Integer> getRatings(Instant time, Set<String> userJids) {
        Optional<UserRatingEventModel> ratingEvent = ratingEventDao.selectLatestBefore(time);
        if (!ratingEvent.isPresent()) {
            return ImmutableMap.of();
        }
        return ratingDao.selectAllByTimeAndUserJids(ratingEvent.get().time, userJids).entrySet()
                .stream()
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().publicRating));
    }
}
