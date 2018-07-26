package judgels.jophiel.user.rating;

import com.google.common.collect.Lists;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.jophiel.persistence.UserRatingDao;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;

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

    public Page<UserWithRating> getTopRatings(Instant time, SelectionOptions options) {
        return ratingDao.selectTopPagedByTime(time, options)
                .mapData(data -> Lists.transform(data, m -> UserWithRating.of(m.userJid, m.publicRating)));
    }
}
