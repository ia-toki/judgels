package judgels.jophiel.api.user;

import static com.palantir.remoting.api.testing.Assertions.assertThatRemoteExceptionThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.awaitility.Awaitility.await;

import com.palantir.remoting.api.errors.ErrorType;
import java.util.concurrent.TimeUnit;
import judgels.jophiel.api.AbstractServiceIntegrationTests;
import judgels.jophiel.api.session.Credentials;
import judgels.jophiel.api.session.SessionService;
import org.junit.jupiter.api.Test;
import org.subethamail.wiser.Wiser;

class UserServiceIntegrationTests extends AbstractServiceIntegrationTests {
    private UserService userService = createService(UserService.class);
    private SessionService sessionService = createService(SessionService.class);

    @Test void basic_flow() {
        assertThatRemoteExceptionThrownBy(() -> userService.getUser("userJid"))
                .isGeneratedFromErrorType(ErrorType.NOT_FOUND);
        assertThatRemoteExceptionThrownBy(() -> userService.getUserByUsername("username"))
                .isGeneratedFromErrorType(ErrorType.NOT_FOUND);

        User user = userService.createUser(adminHeader, new UserData.Builder()
                .username("username")
                .password("password")
                .email("email@domain.com")
                .name("First Last")
                .build());

        assertThat(user).isEqualTo(userService.getUserByUsername("username"));
        assertThat(user.getUsername()).isEqualTo("username");

        assertThat(userService.getUser(user.getJid())).isEqualTo(user);
    }

    @Test void register_flow() {
        Wiser wiser = new Wiser();
        wiser.setPort(2500);
        wiser.start();

        userService.registerUser(new UserData.Builder()
                .username("useruser")
                .password("password")
                .email("user@domain.com")
                .name("User User")
                .build());
        Credentials credentials = Credentials.of("useruser", "password");

        await()
                .atMost(30, TimeUnit.SECONDS)
                .pollInterval(1, TimeUnit.SECONDS)
                .until(() -> !wiser.getMessages().isEmpty());

        assertThatRemoteExceptionThrownBy(() -> sessionService.logIn(credentials))
                .isGeneratedFromErrorType(ErrorType.PERMISSION_DENIED);

        String[] lines = new String(wiser.getMessages().get(0).getData()).split("\n");
        String emailCode = lines[lines.length - 1];

        userService.activateUser(emailCode);
        assertThatCode(() -> sessionService.logIn(credentials))
                .doesNotThrowAnyException();

        wiser.stop();
    }
}
