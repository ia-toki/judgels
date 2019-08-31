package judgels.uriel.api.contest;

import java.util.Optional;

public class ContestDivisions {
    private ContestDivisions() {}

    public static boolean isRatingInDivision(Optional<Integer> rating, int division) {
        if (division == 2) {
            return rating.orElse(0) < 2000;
        }
        if (division == 1) {
            return rating.orElse(0) >= 2000;
        }
        return false;
    }
}
