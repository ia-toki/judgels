package judgels.jophiel.api.user.me;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import judgels.jophiel.api.AbstractServiceIntegrationTests;
import judgels.jophiel.api.role.JophielRole;
import judgels.jophiel.api.session.Credentials;
import judgels.jophiel.api.session.SessionService;
import judgels.jophiel.api.user.UserData;
import judgels.jophiel.api.user.UserService;
import judgels.service.api.actor.AuthHeader;
import org.junit.jupiter.api.Test;

class MyUserServiceIntegrationTests extends AbstractServiceIntegrationTests {
    private UserService userService = createService(UserService.class);
    private MyUserService myUserService = createService(MyUserService.class);
    private SessionService sessionService = createService(SessionService.class);

    @Test
    void get_myself() {
        assertThat(myUserService.getMyself(adminHeader).getUsername()).isEqualTo("superadmin");
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
        assertThatThrownBy(() -> myUserService.updateMyPassword(authHeader, wrongData))
                .hasFieldOrPropertyWithValue("code", 400);

        assertThatCode(() -> sessionService.logIn(Credentials.of("charlie", "pass")))
                .doesNotThrowAnyException();

        assertThatThrownBy(() -> sessionService.logIn(Credentials.of("charlie", "newPass")))
                .hasFieldOrPropertyWithValue("code", 403);

        PasswordUpdateData correctData = PasswordUpdateData.of("pass", "newPass");
        myUserService.updateMyPassword(authHeader, correctData);

        assertThatThrownBy(() -> sessionService.logIn(Credentials.of("charlie", "pass")))
                .hasFieldOrPropertyWithValue("code", 403);

        assertThatCode(() -> sessionService.logIn(Credentials.of("charlie", "newPass")))
                .doesNotThrowAnyException();
    }

    @Test
    void get_role() {
        assertThat(myUserService.getMyRole(adminHeader).getJophiel()).isEqualTo(JophielRole.SUPERADMIN);

        AuthHeader charlieHeader = AuthHeader.of(sessionService.logIn(Credentials.of("charlie", "newPass")).getToken());
        assertThat(myUserService.getMyRole(charlieHeader).getJophiel()).isEqualTo(JophielRole.USER);
    }
}
