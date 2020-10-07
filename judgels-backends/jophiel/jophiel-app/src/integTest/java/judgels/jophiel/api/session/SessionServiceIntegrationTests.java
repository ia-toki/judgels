package judgels.jophiel.api.session;

import static com.palantir.conjure.java.api.testing.Assertions.assertThatRemoteExceptionThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.palantir.conjure.java.api.errors.ErrorType;
import judgels.jophiel.api.AbstractServiceIntegrationTests;
import judgels.jophiel.api.user.User;
import judgels.jophiel.api.user.UserData;
import judgels.jophiel.api.user.UserService;
import judgels.service.api.actor.AuthHeader;
import org.junit.jupiter.api.Test;
import org.openjdk.tools.javac.util.List;

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

    @Test
    void batch_logout() {
        User userToLogout1 = userService.createUser(adminHeader, new UserData.Builder()
                .username("user_to_logout_1")
                .password("pass1")
                .email("user_to_logout_1@domain.com")
                .build());
        User userToLogout2 = userService.createUser(adminHeader, new UserData.Builder()
                .username("user_to_logout_2")
                .password("pass2")
                .email("user_to_logout_2@domain.com")
                .build());
        User userToNotLogout = userService.createUser(adminHeader, new UserData.Builder()
                .username("user_to_not_logout")
                .password("pass3")
                .email("user_to_not_logout@domain.com")
                .build());

        Session session1 = sessionService.logIn(Credentials.of("user_to_logout_1", "pass1"));
        Session session2 = sessionService.logIn(Credentials.of("user_to_logout_2", "pass2"));
        Session session3 = sessionService.logIn(Credentials.of("user_to_not_logout", "pass3"));
        assertThat(session1.getUserJid()).isEqualTo(userToLogout1.getJid());
        assertThat(session2.getUserJid()).isEqualTo(userToLogout2.getJid());
        assertThat(session3.getUserJid()).isEqualTo(userToNotLogout.getJid());

        sessionService.batchLogout(
                adminHeader, BatchLogoutData.of(List.of(session1.getUserJid(), session2.getUserJid())));

        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> userService.getUser(AuthHeader.of(session1.getToken()), userToLogout1.getJid()))
                .withMessageContaining("Judgels:Unauthorized");
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> userService.getUser(AuthHeader.of(session2.getToken()), userToLogout2.getJid()))
                .withMessageContaining("Judgels:Unauthorized");
        assertThat(userService.getUser(AuthHeader.of(session3.getToken()), userToNotLogout.getJid()))
                .isEqualTo(userToNotLogout);
    }
}
