package judgels.jophiel.api.session;

import static com.palantir.remoting.api.testing.Assertions.assertThatRemoteExceptionThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.palantir.remoting.api.errors.ErrorType;
import judgels.jophiel.api.AbstractServiceIntegrationTests;
import judgels.jophiel.api.user.User;
import judgels.jophiel.api.user.UserData;
import judgels.jophiel.api.user.UserService;
import judgels.service.api.actor.AuthHeader;
import org.junit.jupiter.api.Test;

class SessionServiceIntegrationTests extends AbstractServiceIntegrationTests {
    private SessionService sessionService = createService(SessionService.class);
    private UserService userService = createService(UserService.class);

    @Test
    void login_logout_flow() {
        Credentials userCredentials = Credentials.of("user", "password");

        assertThatRemoteExceptionThrownBy(() -> sessionService.logIn(userCredentials))
                .isGeneratedFromErrorType(ErrorType.PERMISSION_DENIED);

        User user = userService.createUser(adminHeader, new UserData.Builder()
                .username("user")
                .password("password")
                .email("user@domain.com")
                .build());

        assertThatRemoteExceptionThrownBy(() -> sessionService.logIn(Credentials.of("user", "wrong")))
                .isGeneratedFromErrorType(ErrorType.PERMISSION_DENIED);

        Session session = sessionService.logIn(userCredentials);
        assertThat(session.getUserJid()).isEqualTo(user.getJid());

        String sessionToken = session.getToken();
        assertThat(userService.getUser(AuthHeader.of(sessionToken), user.getJid())).isEqualTo(user);

        sessionService.logOut(AuthHeader.of(session.getToken()));

        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> userService.getUser(AuthHeader.of(sessionToken), user.getJid()))
                .withMessageContaining("Judgels:Unauthorized");

        // test login with email
        assertThatCode(() -> sessionService.logIn(Credentials.of("user@domain.com", "password")))
                .doesNotThrowAnyException();
    }
}
