package judgels.jophiel.user.rating;

import com.google.common.collect.Lists;
import jakarta.inject.Inject;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import judgels.jophiel.api.user.rating.RatingEvent;
import judgels.jophiel.api.user.rating.UserRating;
import judgels.jophiel.api.user.rating.UserRatingEvent;
import judgels.jophiel.persistence.UserRatingDao;
import judgels.jophiel.persistence.UserRatingEventDao;
import judgels.jophiel.persistence.UserRatingEventModel;
import judgels.jophiel.persistence.UserRatingModel;
import judgels.persistence.api.Page;

public class UserRatingStore {
    private final UserRatingDao ratingDao;
    private final UserRatingEventDao ratingEventDao;

    @Inject
    public UserRatingStore(UserRatingDao ratingDao, UserRatingEventDao ratingEventDao) {
        this.ratingDao = ratingDao;
        this.ratingEventDao = ratingEventDao;
    }

    public Map<String, UserRating> getRatings(Instant time, Collection<String> userJids) {
        return ratingDao.selectAllByTimeAndUserJids(time, userJids)
                .stream()
                .collect(Collectors.toMap(m -> m.userJid, UserRatingStore::fromModel));
    }

    public Page<UserWithRating> getTopRatings(Instant time, int pageNumber, int pageSize) {
        return ratingDao.selectTopPagedByTime(time, pageNumber, pageSize).mapPage(
                p -> Lists.transform(p, m -> UserWithRating.of(m.userJid, fromModel(m))));
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

    public Optional<RatingEvent> getLatestRatingEvent() {
        return ratingEventDao.select().latest().map(UserRatingStore::fromModel);
    }

    public List<UserRatingEvent> getUserRatingEvents(String userJid) {
        List<UserRatingModel> ratings = ratingDao.selectAllByUserJid(userJid);

        var times = Lists.transform(ratings, e -> e.time);
        Map<Instant, UserRatingEventModel> ratingEventsMap = ratingEventDao
                .selectAllByTimes(times)
                .stream()
                .collect(Collectors.toMap(m -> m.time, m -> m));

        return Lists.transform(ratings, e -> new UserRatingEvent.Builder()
                .time(e.time)
                .eventJid(ratingEventsMap.get(e.time).eventJid)
                .rating(fromModel(e))
                .build());
    }

    private static UserRating fromModel(UserRatingModel model) {
        return new UserRating.Builder()
                .publicRating(model.publicRating)
                .hiddenRating(model.hiddenRating)
                .build();
    }

    private static RatingEvent fromModel(UserRatingEventModel model) {
        return new RatingEvent.Builder()
                .time(model.time)
                .eventJid(model.eventJid)
                .build();
    }
}
