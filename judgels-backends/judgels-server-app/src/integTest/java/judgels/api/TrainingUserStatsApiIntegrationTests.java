package judgels.api;

import static org.assertj.core.api.Assertions.assertThat;

import judgels.api.training.stats.UserStats;
import judgels.training.stats.TrainingUserStatsClient;
import org.junit.jupiter.api.Test;

class TrainingUserStatsApiIntegrationTests extends BaseTrainingApiIntegrationTests {
    private final TrainingUserStatsClient userStatsClient = createClient(TrainingUserStatsClient.class);

    @Test
    void get_user_stats() {
        UserStats stats = userStatsClient.getUserStats(USER_A);
        assertThat(stats.getTotalScores()).isZero();
        assertThat(stats.getTotalProblemsTried()).isZero();
        assertThat(stats.getTotalProblemVerdictsMap()).isEmpty();

        assertNotFound(() -> userStatsClient.getUserStats("bogus"));
    }

    @Test
    void get_top_user_stats() {
        var response = userStatsClient.getTopUserStats(1, 10);
        assertThat(response.getData().getPage()).isEmpty();
        assertThat(response.getProfilesMap()).isEmpty();
    }
}
