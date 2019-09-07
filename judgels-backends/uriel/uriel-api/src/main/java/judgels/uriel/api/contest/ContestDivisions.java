package judgels.uriel.api.contest;

import java.util.Optional;
import judgels.jophiel.api.user.rating.UserRating;

public class ContestDivisions {
    private ContestDivisions() {}

    public static boolean isRatingInDivision(Optional<UserRating> rating, int division) {
        int publicRating = rating.map(UserRating::getPublicRating).orElse(UserRating.INITIAL_RATING);
        if (division == 2) {
            return publicRating < 2000;
        }
        if (division == 1) {
            return publicRating >= 2000;
        }
        return false;
    }
}
