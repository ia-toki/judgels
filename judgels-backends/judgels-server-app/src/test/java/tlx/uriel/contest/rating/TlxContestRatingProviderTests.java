package tlx.uriel.contest.rating;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import judgels.jophiel.api.user.rating.UserRating;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class TlxContestRatingProviderTests {
    private TlxContestRatingProvider provider;

    @BeforeEach
    void before() {
        provider = new TlxContestRatingProvider();
    }

    @Nested
    class is_rating_in_division {
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
                rating = Optional.of(UserRating.of(publicRating, publicRating));
            }
            assertThat(provider.isRatingInDivision(rating, division)).isEqualTo(result);
        }
    }

    @Test
    void get_updated_ratings() {
        List<String> contestJids = List.of("a", "b", "c", "d");
        Map<String, Integer> ranksMap = Map.of(
                "a", 1,
                "b", 2,
                "c", 2,
                "d", 4);
        Map<String, UserRating> currentRatingsMap = Map.of(
                "a", UserRating.of(1500, 1500),
                "b", UserRating.of(2100, 2000),
                "c", UserRating.of(1700, 1700),
                "d", UserRating.of(2200, 2200));

        assertThat(provider.getUpdatedRatings(contestJids, ranksMap, currentRatingsMap))
                .isEqualTo(Map.of(
                    "a", UserRating.of(1656, 1656),
                    "b", UserRating.of(2041, 1982),
                    "c", UserRating.of(1712, 1712),
                    "d", UserRating.of(2124, 2048)));
    }
}
