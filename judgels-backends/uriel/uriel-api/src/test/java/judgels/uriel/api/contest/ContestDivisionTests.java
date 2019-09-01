package judgels.uriel.api.contest;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.junit.jupiter.api.Test;

class ContestDivisionTests {
    @Test
    void is_rating_in_division_2() {
        assertThat(ContestDivisions.isRatingInDivision(Optional.empty(), 2)).isTrue();
        assertThat(ContestDivisions.isRatingInDivision(Optional.of(100), 2)).isTrue();
        assertThat(ContestDivisions.isRatingInDivision(Optional.of(1999), 2)).isTrue();
        assertThat(ContestDivisions.isRatingInDivision(Optional.of(2000), 2)).isFalse();
        assertThat(ContestDivisions.isRatingInDivision(Optional.of(2001), 2)).isFalse();
        assertThat(ContestDivisions.isRatingInDivision(Optional.of(2500), 2)).isFalse();
    }

    @Test
    void is_rating_in_division_1() {
        assertThat(ContestDivisions.isRatingInDivision(Optional.empty(), 1)).isFalse();
        assertThat(ContestDivisions.isRatingInDivision(Optional.of(100), 1)).isFalse();
        assertThat(ContestDivisions.isRatingInDivision(Optional.of(1999), 1)).isFalse();
        assertThat(ContestDivisions.isRatingInDivision(Optional.of(2000), 1)).isTrue();
        assertThat(ContestDivisions.isRatingInDivision(Optional.of(2001), 1)).isTrue();
        assertThat(ContestDivisions.isRatingInDivision(Optional.of(2500), 1)).isTrue();
    }
}
