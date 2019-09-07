package judgels.uriel.api.contest;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import judgels.jophiel.api.user.rating.UserRating;
import org.junit.jupiter.api.Test;

class ContestDivisionTests {
    @Test
    void is_rating_in_division_2() {
        test(0, 2, true);
        test(100, 2, true);
        test(1999, 2, true);
        test(2000, 2, false);
        test(2001, 2, false);
        test(2500, 2, false);
    }

    @Test
    void is_rating_in_division_1() {
        test(0, 1, false);
        test(100, 1, false);
        test(1999, 1, false);
        test(2000, 1, true);
        test(2001, 1, true);
        test(2500, 1, true);
    }

    private void test(int publicRating, int division, boolean result) {
        Optional<UserRating> rating = Optional.empty();
        if (publicRating > 0) {
            rating = Optional.of(
                    new UserRating.Builder().publicRating(publicRating).hiddenRating(publicRating).build());
        }
        assertThat(ContestDivisions.isRatingInDivision(rating, division)).isEqualTo(result);
    }
}
