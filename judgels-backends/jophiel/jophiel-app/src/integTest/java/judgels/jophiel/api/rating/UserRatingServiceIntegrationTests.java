package judgels.jophiel.api.rating;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import judgels.jophiel.api.AbstractServiceIntegrationTests;
import judgels.jophiel.api.user.User;
import judgels.jophiel.api.user.UserData;
import judgels.jophiel.api.user.UserService;
import judgels.jophiel.api.user.rating.UserRating;
import judgels.jophiel.api.user.rating.UserRatingEvent;
import judgels.jophiel.api.user.rating.UserRatingService;
import judgels.jophiel.api.user.rating.UserRatingUpdateData;
import org.junit.jupiter.api.Test;

public class UserRatingServiceIntegrationTests extends AbstractServiceIntegrationTests {
    private UserService userService = createService(UserService.class);
    private UserRatingService userRatingService = createService(UserRatingService.class);

    @Test
    void end_to_end_flow() {
        User andi = userService.createUser(adminHeader, new UserData.Builder()
                .username("andi")
                .password("andi-pass")
                .email("andi@domain.com")
                .build());

        User budi = userService.createUser(adminHeader, new UserData.Builder()
                .username("budi")
                .password("budi-pass")
                .email("budi@domain.com")
                .build());

        Instant firstTime = Instant.ofEpochSecond(10);
        Instant secondTime = Instant.ofEpochSecond(100);

        userRatingService.updateRatings(adminHeader, new UserRatingUpdateData.Builder()
                .time(firstTime)
                .eventJid("open-contest-1-jid")
                .putRatingsMap(
                        andi.getUsername(),
                        new UserRating.Builder().publicRating(2000).hiddenRating(1000).build())
                .putRatingsMap(
                        budi.getUsername(),
                        new UserRating.Builder().publicRating(10).hiddenRating(20).build())
                .build());

        userRatingService.updateRatings(adminHeader, new UserRatingUpdateData.Builder()
                .time(secondTime)
                .eventJid("open-contest-2-jid")
                .putRatingsMap(
                        andi.getUsername(),
                        new UserRating.Builder().publicRating(3000).hiddenRating(1500).build())
                .build());

        assertThat(userRatingService.getRatingHistory(andi.getJid())).containsOnly(
                new UserRatingEvent.Builder()
                        .time(firstTime)
                        .eventJid("open-contest-1-jid")
                        .userJid(andi.getJid())
                        .rating(2000)
                        .build(),
                new UserRatingEvent.Builder()
                        .time(secondTime)
                        .eventJid("open-contest-2-jid")
                        .userJid(andi.getJid())
                        .rating(3000)
                        .build());

        assertThat(userRatingService.getRatingHistory(budi.getJid())).containsOnly(
                new UserRatingEvent.Builder()
                        .time(firstTime)
                        .eventJid("open-contest-1-jid")
                        .userJid(budi.getJid())
                        .rating(10)
                        .build());
    }
}
