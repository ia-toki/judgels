package judgels.jophiel.user.rating;

import com.google.common.collect.Lists;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.jophiel.api.user.rating.UserRating;
import judgels.jophiel.api.user.rating.UserRatingEvent;
import judgels.jophiel.persistence.UserRatingDao;
import judgels.jophiel.persistence.UserRatingEventDao;
import judgels.jophiel.persistence.UserRatingEventModel;
import judgels.jophiel.persistence.UserRatingModel;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;

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
        return ratingDao.selectAllByTimeAndUserJids(time, userJids).entrySet()
                .stream()
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().publicRating));
    }

    public Page<UserWithRating> getTopRatings(Instant time, SelectionOptions options) {
        return ratingDao.selectTopPagedByTime(time, options).mapPage(
                p -> Lists.transform(p, m -> UserWithRating.of(m.userJid, m.publicRating)));
    }

    public void updateRatings(Instant time, String eventJid, Map<String, UserRating> ratingsMap) {
        UserRatingEventModel eventModel = new UserRatingEventModel();
        eventModel.time = time;
        eventModel.eventJid = eventJid;
        ratingEventDao.insert(eventModel);

        ratingsMap.forEach((userJid, rating) -> {
            UserRatingModel ratingModel = new UserRatingModel();
            ratingModel.userJid = userJid;
            ratingModel.time = time;
            ratingModel.publicRating = rating.getPublicRating();
            ratingModel.hiddenRating = rating.getHiddenRating();
            ratingDao.insert(ratingModel);
        });
    }

    public List<UserRatingEvent> getRatingEvents(String userJid) {
        List<UserRatingModel> ratings = ratingDao.selectAllByUserJid(userJid);
        Map<Instant, UserRatingEventModel> ratingEventsMap =
                ratingEventDao.selectAllByTimes(
                        ratings.stream()
                                .map(e -> e.time)
                                .collect(Collectors.toSet()));
        return ratings.stream()
                .map(e -> new UserRatingEvent.Builder()
                        .time(e.time)
                        .eventJid(ratingEventsMap.get(e.time).eventJid)
                        .userJid(e.userJid)
                        .rating(e.publicRating)
                        .build()
                ).collect(Collectors.toList());
    }
}
