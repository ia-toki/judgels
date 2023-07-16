package judgels.jophiel.api;

import static org.assertj.core.api.Assertions.assertThat;

import judgels.BaseJudgelsApiIntegrationTests;
import judgels.jophiel.SessionClient;
import judgels.jophiel.UserClient;
import judgels.jophiel.api.session.Credentials;
import judgels.jophiel.api.session.Session;
import judgels.jophiel.api.user.User;
import org.junit.jupiter.api.Test;

public class SessionApiIntegrationTests extends BaseJudgelsApiIntegrationTests {
    private final SessionClient sessionClient = createClient(SessionClient.class);
    private final UserClient userClient = createClient(UserClient.class);

    @Test
    void login_logout() {
        // log in to nonexistent user
        Credentials credentials = Credentials.of("andi", "pass");
        assertForbidden(() -> sessionClient.logIn(credentials));

        User user = createUser("andi");

        // log in with wrong password
        assertForbidden(() -> sessionClient.logIn(Credentials.of("andi", "wrong")));

        // log in with correct password
        Session session = sessionClient.logIn(credentials);
        assertThat(session.getUserJid()).isEqualTo(user.getJid());

        String token = session.getToken();
        assertThat(token).isNotEmpty();

        // assert that session token is valid
        assertPermitted(() -> userClient.getUser(token, user.getJid()));

        String anotherToken = sessionClient.logIn(credentials).getToken();

        // assert that session tokens are unique
        assertThat(token).isNotEqualTo(anotherToken);

        sessionClient.logOut(session.getToken());

        // assert that session token is not valid anymore
        assertUnauthorized(() -> userClient.getUser(token, user.getJid()));

        // log in with email
        assertPermitted(() -> sessionClient.logIn(Credentials.of(user.getEmail(), "pass")));
    }
}
