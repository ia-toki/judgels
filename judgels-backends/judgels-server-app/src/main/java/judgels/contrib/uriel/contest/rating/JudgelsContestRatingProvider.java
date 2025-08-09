package judgels.contrib.uriel.contest.rating;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import judgels.jophiel.api.user.rating.UserRating;

public class JudgelsContestRatingProvider implements ContestRatingProvider {
    @Override
    public boolean isRatingInDivision(Optional<UserRating> rating, int division) {
        return false;
    }

    @Override
    public Map<String, UserRating> getUpdatedRatings(List<String> contestantJids, Map<String, Integer> ranksMap, Map<String, UserRating> currentRatingsMap) {
        return currentRatingsMap;
    }
}
