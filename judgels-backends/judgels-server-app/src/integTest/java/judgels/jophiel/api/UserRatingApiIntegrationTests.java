package judgels.jophiel.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import judgels.BaseJudgelsApiIntegrationTests;
import judgels.jophiel.UserRatingClient;
import judgels.jophiel.api.user.User;
import judgels.jophiel.api.user.rating.UserRating;
import judgels.jophiel.api.user.rating.UserRatingEvent;
import judgels.jophiel.api.user.rating.UserRatingUpdateData;
import org.junit.jupiter.api.Test;

public class UserRatingApiIntegrationTests extends BaseJudgelsApiIntegrationTests {
    private final UserRatingClient userRatingClient = createClient(UserRatingClient.class);

    @Test
    void update_ratings() {
        User andi = createUser("andi");
        User budi = createUser("budi");

        Instant firstTime = Instant.ofEpochSecond(10);
        userRatingClient.updateRatings(adminToken, new UserRatingUpdateData.Builder()
                .time(firstTime)
                .eventJid("open-contest-1-jid")
                .putRatingsMap(andi.getJid(), UserRating.of(2000, 1000))
                .putRatingsMap(budi.getJid(), UserRating.of(10, 20))
                .build());

        Instant secondTime = Instant.ofEpochSecond(100);
        userRatingClient.updateRatings(adminToken, new UserRatingUpdateData.Builder()
                .time(secondTime)
                .eventJid("open-contest-2-jid")
                .putRatingsMap(andi.getJid(), UserRating.of(3000, 1500))
                .build());

        assertThat(userRatingClient.getRatingHistory(andi.getJid())).containsOnly(
                new UserRatingEvent.Builder()
                        .time(firstTime)
                        .eventJid("open-contest-1-jid")
                        .rating(UserRating.of(2000, 1000))
                        .build(),
                new UserRatingEvent.Builder()
                        .time(secondTime)
                        .eventJid("open-contest-2-jid")
                        .rating(UserRating.of(3000, 1500))
                        .build());

        assertThat(userRatingClient.getRatingHistory(budi.getJid())).containsOnly(
                new UserRatingEvent.Builder()
                        .time(firstTime)
                        .eventJid("open-contest-1-jid")
                        .rating(UserRating.of(10, 20))
                        .build());
    }
}
