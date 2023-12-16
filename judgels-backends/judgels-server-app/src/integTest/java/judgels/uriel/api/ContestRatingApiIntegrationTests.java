package judgels.uriel.api;

import static org.assertj.core.api.Assertions.assertThat;

import judgels.uriel.ContestRatingClient;
import org.junit.jupiter.api.Test;

class ContestRatingApiIntegrationTests extends BaseUrielApiIntegrationTests {
    private final ContestRatingClient ratingClient = createClient(ContestRatingClient.class);

    @Test
    void get_pending_ratings() {
        assertThat(ratingClient.getContestsPendingRating(superadminToken).getData())
                .isEmpty();
    }
}
