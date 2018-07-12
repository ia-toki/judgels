package judgels.jophiel.api.user;

import static com.palantir.remoting.api.testing.Assertions.assertThatRemoteExceptionThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import com.palantir.remoting.api.errors.ErrorType;
import judgels.jophiel.api.AbstractServiceIntegrationTests;
import judgels.jophiel.api.role.Role;
import judgels.jophiel.api.session.Credentials;
import judgels.jophiel.api.session.SessionService;
import judgels.service.api.actor.AuthHeader;
import org.junit.jupiter.api.Test;

class MyServiceIntegrationTests extends AbstractServiceIntegrationTests {
    private UserService userService = createService(UserService.class);
    private MyService myService = createService(MyService.class);
    private SessionService sessionService = createService(SessionService.class);

    @Test
    void get_myself() {
        assertThat(myService.getMyself(adminHeader).getUsername()).isEqualTo("superadmin");
    }

    @Test
    void update_password_flow() {
        userService.createUser(adminHeader, new UserData.Builder()
                .username("charlie")
                .password("pass")
                .email("charlie@domain.com")
                .build());
        AuthHeader authHeader = AuthHeader.of(sessionService.logIn(Credentials.of("charlie", "pass")).getToken());

        PasswordUpdateData wrongData = PasswordUpdateData.of("wrongPass", "newPass");
        assertThatRemoteExceptionThrownBy(() -> myService.updateMyPassword(authHeader, wrongData))
                .isGeneratedFromErrorType(ErrorType.INVALID_ARGUMENT);

        assertThatCode(() -> sessionService.logIn(Credentials.of("charlie", "pass")))
                .doesNotThrowAnyException();

        assertThatRemoteExceptionThrownBy(() -> sessionService.logIn(Credentials.of("charlie", "newPass")))
                .isGeneratedFromErrorType(ErrorType.PERMISSION_DENIED);

        PasswordUpdateData correctData = PasswordUpdateData.of("pass", "newPass");
        myService.updateMyPassword(authHeader, correctData);

        assertThatRemoteExceptionThrownBy(() -> sessionService.logIn(Credentials.of("charlie", "pass")))
                .isGeneratedFromErrorType(ErrorType.PERMISSION_DENIED);

        assertThatCode(() -> sessionService.logIn(Credentials.of("charlie", "newPass")))
                .doesNotThrowAnyException();
    }

    @Test
    void get_role() {
        assertThat(myService.getMyRole(adminHeader)).isEqualTo(Role.SUPERADMIN);

        AuthHeader charlieHeader = AuthHeader.of(sessionService.logIn(Credentials.of("charlie", "newPass")).getToken());
        assertThat(myService.getMyRole(charlieHeader)).isEqualTo(Role.USER);
    }
}
