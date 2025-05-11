package tlx.uriel.api.contest;

import java.util.Optional;
import judgels.jophiel.api.user.rating.UserRating;
import tlx.jophiel.api.user.rating.TlxRating;

public class TlxDivisions {
    private TlxDivisions() {}

    public static boolean isRatingInDivision(Optional<UserRating> rating, int division) {
        int publicRating = rating.map(UserRating::getPublicRating).orElse(TlxRating.INITIAL_RATING);
        if (division == 2) {
            return publicRating < 2000;
        }
        if (division == 1) {
            return publicRating >= 2000;
        }
        return false;
    }
}
