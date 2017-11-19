package judgels.jophiel.api.account;

import static com.palantir.remoting.api.testing.Assertions.assertThatRemoteExceptionThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.palantir.remoting.api.errors.ErrorType;
import judgels.jophiel.api.AbstractServiceIntegrationTests;
import judgels.jophiel.api.session.Session;
import judgels.jophiel.api.user.User;
import judgels.jophiel.api.user.UserService;
import judgels.service.api.actor.AuthHeader;
import org.junit.jupiter.api.Test;

class AccountServiceIntegrationTests extends AbstractServiceIntegrationTests {
    private AccountService accountService = createService(AccountService.class);
    private UserService userService = createService(UserService.class);

    @Test void login_logout_flow() {
        assertThatRemoteExceptionThrownBy(() -> accountService.logIn(Credentials.of("user", "password")))
                .isGeneratedFromErrorType(ErrorType.PERMISSION_DENIED);

        User user = userService.createUser(new User.Data.Builder()
                .username("user")
                .email("user@domain.com")
                .name("User")
                .build());

        Session session = accountService.logIn(Credentials.of("user", "password"));
        assertThat(session.getUserJid()).isEqualTo(user.getJid());

        String sessionToken = session.getToken();
        assertThat(userService.getMyself(AuthHeader.of(sessionToken))).isEqualTo(user);

        accountService.logOut(AuthHeader.of(session.getToken()));

        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> userService.getMyself(AuthHeader.of(sessionToken)))
                .withMessageContaining("401");
    }
}
