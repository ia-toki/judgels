package judgels.jophiel.api.password;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import com.google.common.collect.ImmutableMap;
import judgels.jophiel.api.AbstractServiceIntegrationTests;
import judgels.jophiel.api.session.Credentials;
import judgels.jophiel.api.session.SessionService;
import judgels.jophiel.api.user.UserData;
import judgels.jophiel.api.user.UserService;
import judgels.jophiel.api.user.password.PasswordsUpdateResponse;
import judgels.jophiel.api.user.password.UserPasswordService;
import org.junit.jupiter.api.Test;

class UserPasswordServiceIntegrationTests extends AbstractServiceIntegrationTests {
    private UserService userService = createService(UserService.class);
    private UserPasswordService passwordService = createService(UserPasswordService.class);
    private SessionService sessionService = createService(SessionService.class);

    @Test
    void end_to_end_flow() {
        userService.createUser(adminHeader, new UserData.Builder()
                .username("andi")
                .password("andi-pass")
                .email("andi@domain.com")
                .build());
        userService.createUser(adminHeader, new UserData.Builder()
                .username("budi")
                .password("budi-pass")
                .email("budi@domain.com")
                .build());

        assertThatCode(() -> sessionService.logIn(Credentials.of("andi", "andi-pass")))
                .doesNotThrowAnyException();

        assertThatCode(() -> sessionService.logIn(Credentials.of("budi", "budi-pass")))
                .doesNotThrowAnyException();

        PasswordsUpdateResponse response = passwordService.updateUserPasswords(adminHeader, ImmutableMap.of(
                "andi", "new-andi-pass",
                "budi", "new-budi-pass",
                "caca", "new-caca-pass"));

        assertThat(response.getUpdatedUserProfilesMap()).containsKeys("andi", "budi");
        assertThat(response.getUpdatedUserProfilesMap().get("andi").getUsername()).isEqualTo("andi");

        assertThatCode(() -> sessionService.logIn(Credentials.of("andi", "new-andi-pass")))
                .doesNotThrowAnyException();

        assertThatCode(() -> sessionService.logIn(Credentials.of("budi", "new-budi-pass")))
                .doesNotThrowAnyException();
    }
}
