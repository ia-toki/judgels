package judgels.jophiel.api.rating;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import judgels.jophiel.api.AbstractServiceIntegrationTests;
import judgels.jophiel.api.user.User;
import judgels.jophiel.api.user.rating.RatingEvent;
import judgels.jophiel.api.user.rating.UserRating;
import judgels.jophiel.api.user.rating.UserRatingEvent;
import judgels.jophiel.api.user.rating.UserRatingService;
import judgels.jophiel.api.user.rating.UserRatingUpdateData;
import org.junit.jupiter.api.Test;

public class UserRatingServiceIntegrationTests extends AbstractServiceIntegrationTests {
    private final UserRatingService userRatingService = createService(UserRatingService.class);

    @Test
    void update_ratings() {
        User andi = createUser("andi");
        User budi = createUser("budi");

        Instant firstTime = Instant.ofEpochSecond(10);
        Instant secondTime = Instant.ofEpochSecond(100);

        assertThat(userRatingService.getLatestRatingEvent()).isEmpty();

        userRatingService.updateRatings(adminHeader, new UserRatingUpdateData.Builder()
                .time(firstTime)
                .eventJid("open-contest-1-jid")
                .putRatingsMap(
                        andi.getJid(),
                        new UserRating.Builder().publicRating(2000).hiddenRating(1000).build())
                .putRatingsMap(
                        budi.getJid(),
                        new UserRating.Builder().publicRating(10).hiddenRating(20).build())
                .build());

        assertThat(userRatingService.getLatestRatingEvent()).contains(new RatingEvent.Builder()
                .time(firstTime)
                .eventJid("open-contest-1-jid")
                .build());

        userRatingService.updateRatings(adminHeader, new UserRatingUpdateData.Builder()
                .time(secondTime)
                .eventJid("open-contest-2-jid")
                .putRatingsMap(
                        andi.getJid(),
                        new UserRating.Builder().publicRating(3000).hiddenRating(1500).build())
                .build());

        assertThat(userRatingService.getLatestRatingEvent()).contains(new RatingEvent.Builder()
                .time(secondTime)
                .eventJid("open-contest-2-jid")
                .build());

        assertThat(userRatingService.getRatingHistory(andi.getJid())).containsOnly(
                new UserRatingEvent.Builder()
                        .time(firstTime)
                        .eventJid("open-contest-1-jid")
                        .rating(new UserRating.Builder().publicRating(2000).hiddenRating(1000).build())
                        .build(),
                new UserRatingEvent.Builder()
                        .time(secondTime)
                        .eventJid("open-contest-2-jid")
                        .rating(new UserRating.Builder().publicRating(3000).hiddenRating(1500).build())
                        .build());

        assertThat(userRatingService.getRatingHistory(budi.getJid())).containsOnly(
                new UserRatingEvent.Builder()
                        .time(firstTime)
                        .eventJid("open-contest-1-jid")
                        .rating(new UserRating.Builder().publicRating(10).hiddenRating(20).build())
                        .build());
    }
}
