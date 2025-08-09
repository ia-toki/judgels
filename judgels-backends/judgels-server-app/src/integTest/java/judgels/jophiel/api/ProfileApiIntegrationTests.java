package judgels.jophiel.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.List;
import judgels.BaseJudgelsApiIntegrationTests;
import judgels.contrib.jophiel.UserRatingClient;
import judgels.contrib.jophiel.api.user.rating.UserRatingUpdateData;
import judgels.jophiel.ProfileClient;
import judgels.jophiel.UserClient;
import judgels.jophiel.api.profile.BasicProfile;
import judgels.jophiel.api.profile.Profile;
import judgels.jophiel.api.user.User;
import judgels.jophiel.api.user.rating.UserRating;
import org.junit.jupiter.api.Test;

public class ProfileApiIntegrationTests extends BaseJudgelsApiIntegrationTests {
    private final ProfileClient profileClient = createClient(ProfileClient.class);
    private final UserClient userClient = createClient(UserClient.class);
    private final UserRatingClient userRatingClient = createClient(UserRatingClient.class);

    @Test
    void get_profiles() {
        User andi = createUser("andi");
        User budi = createUser("budi");
        createUser("caca");

        userClient.upsertUsers(adminToken, "username,name,country\n"
                + "andi,Andi,ID\n"
                + "caca,Caca,SG\n");

        // basic profiles without ratings

        BasicProfile andiBasicProfile = profileClient.getBasicProfile(andi.getJid());
        assertThat(andiBasicProfile).isEqualTo(new BasicProfile.Builder()
                .username("andi")
                .country("ID")
                .name("Andi")
                .build());

        BasicProfile budiBasicProfile = profileClient.getBasicProfile(budi.getJid());
        assertThat(budiBasicProfile).isEqualTo(new BasicProfile.Builder()
                .username("budi")
                .build());

        Instant firstTime = Instant.ofEpochSecond(10);
        userRatingClient.updateRatings(adminToken, new UserRatingUpdateData.Builder()
                .time(firstTime)
                .eventJid("contest-1-jid")
                .putRatingsMap(andi.getJid(), UserRating.of(2000, 1000))
                .putRatingsMap(budi.getJid(), UserRating.of(1800, 1700))
                .build());

        Instant secondTime = Instant.ofEpochSecond(100);
        userRatingClient.updateRatings(adminToken, new UserRatingUpdateData.Builder()
                .time(secondTime)
                .eventJid("contest-2-jid")
                .putRatingsMap(andi.getJid(), UserRating.of(2100, 1500))
                .putRatingsMap(budi.getJid(), UserRating.of(2700, 1800))
                .build());

        // basic profiles with latest ratings

        andiBasicProfile = profileClient.getBasicProfile(andi.getJid());
        assertThat(andiBasicProfile).isEqualTo(new BasicProfile.Builder()
                .username("andi")
                .country("ID")
                .name("Andi")
                .rating(UserRating.of(2100, 1500))
                .build());

        budiBasicProfile = profileClient.getBasicProfile(budi.getJid());
        assertThat(budiBasicProfile).isEqualTo(new BasicProfile.Builder()
                .username("budi")
                .rating(UserRating.of(2700, 1800))
                .build());

        // top rated profiles

        List<Profile> topProfiles = profileClient.getTopRatedProfiles().getPage();
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
