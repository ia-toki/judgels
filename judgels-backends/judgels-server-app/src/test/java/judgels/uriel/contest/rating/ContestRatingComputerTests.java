package judgels.uriel.contest.rating;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import judgels.jophiel.api.user.rating.UserRating;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestRatingComputerTests {
    private ContestRatingComputer computer;

    @BeforeEach
    void before() {
        computer = new ContestRatingComputer();
    }

    @Test
    void compute() {
        List<String> contestJids = List.of("a", "b", "c", "d");
        Map<String, Integer> ranksMap = Map.of(
                "a", 1,
                "b", 2,
                "c", 2,
                "d", 4);
        Map<String, Integer> publicRatingsMap = Map.of(
                "a", 1500,
                "b", 2100,
                "c", 1700,
                "d", 2200);
        Map<String, Integer> hiddenRatingsMap = Map.of(
                "a", 1500,
                "b", 2000,
                "c", 1700,
                "d", 2200);

        assertThat(computer.compute(contestJids, ranksMap, publicRatingsMap, hiddenRatingsMap))
                .isEqualTo(Map.of(
                    "a", new UserRating.Builder().publicRating(1656).hiddenRating(1656).build(),
                    "b", new UserRating.Builder().publicRating(2041).hiddenRating(1982).build(),
                    "c", new UserRating.Builder().publicRating(1712).hiddenRating(1712).build(),
                    "d", new UserRating.Builder().publicRating(2124).hiddenRating(2048).build()));
    }
}
