package judgels.jophiel.api.user.profile;

import static org.assertj.core.api.Assertions.assertThat;

import judgels.jophiel.api.AbstractServiceIntegrationTests;
import judgels.jophiel.api.user.User;
import judgels.jophiel.api.user.UserData;
import judgels.jophiel.api.user.UserService;
import org.junit.jupiter.api.Test;

class UserProfileServiceIntegrationTests extends AbstractServiceIntegrationTests {
    private UserService userService = createService(UserService.class);
    private UserProfileService profileService = createService(UserProfileService.class);

    @Test
    void basic_flow() {
        User user = userService.createUser(adminHeader, new UserData.Builder()
                .username("alpha")
                .password("pass")
                .email("email@domain.com")
                .build());

        UserProfile profile = new UserProfile.Builder()
                .name("Alpha")
                .gender("MALE")
                .nationality("id")
                .homeAddress("address")
                .shirtSize("L")
                .institution("university")
                .country("nation")
                .province("province")
                .city("town")
                .build();

        profileService.updateProfile(adminHeader, user.getJid(), profile);

        assertThat(profileService.getProfile(adminHeader, user.getJid())).isEqualTo(profile);

        assertThat(profileService.getPublicProfile(user.getJid())).isEqualTo(profile.toPublic("alpha"));
    }
}
