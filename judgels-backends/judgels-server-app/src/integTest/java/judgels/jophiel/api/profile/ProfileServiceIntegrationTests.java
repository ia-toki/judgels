package judgels.jophiel.api.profile;

import static java.util.Optional.empty;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import judgels.jophiel.api.BaseJophielServiceIntegrationTests;
import judgels.jophiel.api.user.User;
import judgels.jophiel.api.user.UserService;
import judgels.jophiel.api.user.rating.UserRating;
import judgels.jophiel.api.user.rating.UserRatingService;
import judgels.jophiel.api.user.rating.UserRatingUpdateData;
import org.junit.jupiter.api.Test;

public class ProfileServiceIntegrationTests extends BaseJophielServiceIntegrationTests {
    private final ProfileService profileService = createService(ProfileService.class);
    private final UserService userService = createService(UserService.class);
    private final UserRatingService userRatingService = createService(UserRatingService.class);

    @Test
    void get_profiles() throws IOException {
        User andi = createUser("andi");
        User budi = createUser("budi");
        createUser("caca");

        userService.upsertUsers(adminHeader, "username,name,country\n"
                + "andi,Andi,ID\n"
                + "caca,Caca,SG\n");

        // basic profiles without ratings

        BasicProfile andiBasicProfile = profileService.getBasicProfile(andi.getJid());
        assertThat(andiBasicProfile).isEqualTo(new BasicProfile.Builder()
                .username("andi")
                .country("ID")
                .name("Andi")
                .build());

        BasicProfile budiBasicProfile = profileService.getBasicProfile(budi.getJid());
        assertThat(budiBasicProfile).isEqualTo(new BasicProfile.Builder()
                .username("budi")
                .build());

        Instant firstTime = Instant.ofEpochSecond(10);
        userRatingService.updateRatings(adminHeader, new UserRatingUpdateData.Builder()
                .time(firstTime)
                .eventJid("contest-1-jid")
                .putRatingsMap(andi.getJid(), UserRating.of(2000, 1000))
                .putRatingsMap(budi.getJid(), UserRating.of(1800, 1700))
                .build());

        Instant secondTime = Instant.ofEpochSecond(100);
        userRatingService.updateRatings(adminHeader, new UserRatingUpdateData.Builder()
                .time(secondTime)
                .eventJid("contest-2-jid")
                .putRatingsMap(andi.getJid(), UserRating.of(2100, 1500))
                .putRatingsMap(budi.getJid(), UserRating.of(2700, 1800))
                .build());

        // basic profiles with latest ratings

        andiBasicProfile = profileService.getBasicProfile(andi.getJid());
        assertThat(andiBasicProfile).isEqualTo(new BasicProfile.Builder()
                .username("andi")
                .country("ID")
                .name("Andi")
                .rating(UserRating.of(2100, 1500))
                .build());

        budiBasicProfile = profileService.getBasicProfile(budi.getJid());
        assertThat(budiBasicProfile).isEqualTo(new BasicProfile.Builder()
                .username("budi")
                .rating(UserRating.of(2700, 1800))
                .build());

        // top rated profiles

        List<Profile> topProfiles = profileService.getTopRatedProfiles(empty(), empty()).getPage();
        assertThat(topProfiles).containsExactly(
                new Profile.Builder()
                        .username("budi")
                        .rating(UserRating.of(2700, 1800))
                        .build(),
                new Profile.Builder()
                        .username("andi")
                        .country("ID")
                        .rating(UserRating.of(2100, 1500))
                        .build());
    }
}
