package judgels.jophiel.api.user;

import static com.palantir.remoting.api.testing.Assertions.assertThatRemoteExceptionThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

import com.palantir.remoting.api.errors.ErrorType;
import judgels.jophiel.api.AbstractServiceIntegrationTests;
import org.junit.jupiter.api.Test;

class UserServiceIntegrationTests extends AbstractServiceIntegrationTests {
    private UserService service = createService(UserService.class);

    @Test void basic_flow() {
        assertThatRemoteExceptionThrownBy(() -> service.getUser("userJid"))
                .isGeneratedFromErrorType(ErrorType.NOT_FOUND);
        assertThatRemoteExceptionThrownBy(() -> service.getUserByUsername("username"))
                .isGeneratedFromErrorType(ErrorType.NOT_FOUND);

        service.createUser(new User.Data.Builder()
                .username("username")
                .email("email@domain.com")
                .name("First Last")
                .build());

        User user = service.getUserByUsername("username");
        assertThat(user.getUsername()).isEqualTo("username");
        assertThat(service.getUser(user.getJid()).getUsername()).isEqualTo("username");
    }
}
