package judgels.jophiel.user.rating;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.jophiel.persistence.UserRatingDao;

@Singleton
public class UserRatingStore {
    private final UserRatingDao ratingDao;

    @Inject
    public UserRatingStore(UserRatingDao ratingDao) {
        this.ratingDao = ratingDao;
    }

    public Map<String, Integer> getRatings(Instant time, Set<String> userJids) {
        return ratingDao.selectAllByTimeAndUserJids(time, userJids).entrySet()
                .stream()
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().publicRating));
    }
}
