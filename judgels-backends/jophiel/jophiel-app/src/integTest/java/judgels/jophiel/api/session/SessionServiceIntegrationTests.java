package judgels.jophiel.api.session;

import static org.assertj.core.api.Assertions.assertThat;

import judgels.jophiel.api.AbstractServiceIntegrationTests;
import judgels.jophiel.api.user.User;
import judgels.jophiel.api.user.UserService;
import judgels.service.api.actor.AuthHeader;
import org.junit.jupiter.api.Test;

public class SessionServiceIntegrationTests extends AbstractServiceIntegrationTests {
    private final SessionService sessionService = createService(SessionService.class);
    private final UserService userService = createService(UserService.class);

    @Test
    void login_logout() {
        // log in to nonexistent user
        Credentials credentials = Credentials.of("andi", "pass");
        assertForbidden(() -> sessionService.logIn(credentials));

        User user = createUser("andi");

        // log in with wrong password
        assertForbidden(() -> sessionService.logIn(Credentials.of("andi", "wrong")));

        // log in with correct password
        Session session = sessionService.logIn(credentials);
        assertThat(session.getUserJid()).isEqualTo(user.getJid());

        String sessionToken = session.getToken();
        assertThat(sessionToken).isNotEmpty();

        // assert that session token is valid
        assertPermitted(() -> userService.getUser(AuthHeader.of(sessionToken), user.getJid()));

        String anotherToken = sessionService.logIn(credentials).getToken();

        // assert that session tokens are unique
        assertThat(sessionToken).isNotEqualTo(anotherToken);

        sessionService.logOut(AuthHeader.of(session.getToken()));

        // assert that session token is not valid anymore
        assertUnauthorized(() -> userService.getUser(AuthHeader.of(sessionToken), user.getJid()));

        // log in with email
        assertPermitted(() -> sessionService.logIn(Credentials.of(user.getEmail(), "pass")));
    }
}
