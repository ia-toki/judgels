package judgels.jophiel.api.session;

import static org.assertj.core.api.Assertions.assertThat;

import judgels.jophiel.api.AbstractServiceIntegrationTests;
import judgels.jophiel.api.user.User;
import judgels.jophiel.api.user.UserService;
import judgels.service.api.actor.AuthHeader;
import org.junit.jupiter.api.Test;

class SessionServiceIntegrationTests extends AbstractServiceIntegrationTests {
    private final SessionService sessionService = createService(SessionService.class);
    private final UserService userService = createService(UserService.class);

    @Test
    void login_logout() {
        Credentials userCredentials = Credentials.of("andi", "pass");

        assertForbidden(() -> sessionService.logIn(userCredentials));

        User user = createUser("andi");

        assertForbidden(() -> sessionService.logIn(Credentials.of("andi", "wrong")));

        Session session = sessionService.logIn(userCredentials);
        assertThat(session.getUserJid()).isEqualTo(user.getJid());

        String sessionToken = session.getToken();
        assertThat(userService.getUser(AuthHeader.of(sessionToken), user.getJid())).isEqualTo(user);

        sessionService.logOut(AuthHeader.of(session.getToken()));

        assertUnauthorized(() -> userService.getUser(AuthHeader.of(sessionToken), user.getJid()));

        // test login with email
        assertPermitted(() -> sessionService.logIn(Credentials.of(user.getEmail(), "pass")));
    }
}
