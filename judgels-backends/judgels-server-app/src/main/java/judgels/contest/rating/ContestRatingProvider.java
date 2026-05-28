package judgels.contest.rating;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import judgels.api.user.rating.UserRating;

public interface ContestRatingProvider {
    boolean isRatingInDivision(Optional<UserRating> rating, int division);
    Map<String, UserRating> getUpdatedRatings(List<String> contestantJids, Map<String, Integer> ranksMap, Map<String, UserRating> currentRatingsMap);
}
