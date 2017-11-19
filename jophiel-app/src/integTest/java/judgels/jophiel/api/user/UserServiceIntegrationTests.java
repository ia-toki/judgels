package judgels.jophiel.api.user;

import static com.palantir.remoting.api.testing.Assertions.assertThatRemoteExceptionThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

import com.palantir.remoting.api.errors.ErrorType;
import judgels.jophiel.api.AbstractServiceIntegrationTests;
import org.junit.jupiter.api.Test;

class UserServiceIntegrationTests extends AbstractServiceIntegrationTests {
    private UserService userService = createService(UserService.class);

    @Test void basic_flow() {
        assertThatRemoteExceptionThrownBy(() -> userService.getUser("userJid"))
                .isGeneratedFromErrorType(ErrorType.NOT_FOUND);
        assertThatRemoteExceptionThrownBy(() -> userService.getUserByUsername("username"))
                .isGeneratedFromErrorType(ErrorType.NOT_FOUND);

        User user = userService.createUser(new User.Data.Builder()
                .username("username")
                .email("email@domain.com")
                .name("First Last")
                .build());

        assertThat(user).isEqualTo(userService.getUserByUsername("username"));
        assertThat(user.getUsername()).isEqualTo("username");

        assertThat(userService.getUser(user.getJid())).isEqualTo(user);
    }
}
