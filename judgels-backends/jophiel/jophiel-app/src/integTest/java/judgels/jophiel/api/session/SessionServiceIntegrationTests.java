package judgels.jophiel.api.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;
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

        assertThatThrownBy(() -> sessionService.logIn(userCredentials))
                .hasFieldOrPropertyWithValue("code", 403);

        User user = userService.createUser(adminHeader, new UserData.Builder()
                .username("user")
                .password("password")
                .email("user@domain.com")
                .build());

        assertThatThrownBy(() -> sessionService.logIn(Credentials.of("user", "wrong")))
                .hasFieldOrPropertyWithValue("code", 403);

        Session session = sessionService.logIn(userCredentials);
        assertThat(session.getUserJid()).isEqualTo(user.getJid());

        String sessionToken = session.getToken();
        assertThat(userService.getUser(AuthHeader.of(sessionToken), user.getJid())).isEqualTo(user);

        sessionService.logOut(AuthHeader.of(session.getToken()));

        assertThatThrownBy(() -> userService.getUser(AuthHeader.of(sessionToken), user.getJid()))
                .hasFieldOrPropertyWithValue("code", 401);

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

        assertThatThrownBy(() -> sessionService.batchLogout(
                AuthHeader.of(session1.getToken()),
                BatchLogoutData.of(Arrays.asList(session1.getUserJid(), session2.getUserJid()))))
                .hasFieldOrPropertyWithValue("code", 403);

        sessionService.batchLogout(
                adminHeader, BatchLogoutData.of(Arrays.asList(session1.getUserJid(), session2.getUserJid())));

        assertThatThrownBy(() -> userService.getUser(AuthHeader.of(session1.getToken()), userToLogout1.getJid()))
                .hasFieldOrPropertyWithValue("code", 401);
        assertThatThrownBy(() -> userService.getUser(AuthHeader.of(session2.getToken()), userToLogout2.getJid()))
                .hasFieldOrPropertyWithValue("code", 401);
        assertThat(userService.getUser(AuthHeader.of(session3.getToken()), userToNotLogout.getJid()))
                .isEqualTo(userToNotLogout);
    }
}
