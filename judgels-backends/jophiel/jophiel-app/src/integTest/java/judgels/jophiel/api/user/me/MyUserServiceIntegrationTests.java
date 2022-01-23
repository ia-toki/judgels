package judgels.jophiel.api.user.me;

import static org.assertj.core.api.Assertions.assertThat;

import judgels.jophiel.api.AbstractServiceIntegrationTests;
import judgels.jophiel.api.role.JophielRole;
import judgels.jophiel.api.session.Credentials;
import judgels.jophiel.api.session.SessionService;
import org.junit.jupiter.api.Test;

class MyUserServiceIntegrationTests extends AbstractServiceIntegrationTests {
    private final MyUserService myUserService = createService(MyUserService.class);
    private final SessionService sessionService = createService(SessionService.class);

    @Test
    void get_myself() {
        assertThat(myUserService.getMyself(adminHeader).getUsername()).isEqualTo("superadmin");
    }

    @Test
    void update_my_password() {
        PasswordUpdateData wrongData = PasswordUpdateData.of("wrongPass", "newPass");
        assertBadRequest(() -> myUserService.updateMyPassword(userHeader, wrongData));
        assertPermitted(() -> sessionService.logIn(Credentials.of("user", "pass")));
        assertForbidden(() -> sessionService.logIn(Credentials.of("user", "newPass")));

        PasswordUpdateData correctData = PasswordUpdateData.of("pass", "newPass");
        myUserService.updateMyPassword(userHeader, correctData);

        assertForbidden(() -> sessionService.logIn(Credentials.of("user", "pass")));
        assertPermitted(() -> sessionService.logIn(Credentials.of("user", "newPass")));
    }

    @Test
    void get_my_role() {
        assertThat(myUserService.getMyRole(adminHeader).getJophiel()).isEqualTo(JophielRole.SUPERADMIN);
        assertThat(myUserService.getMyRole(userHeader).getJophiel()).isEqualTo(JophielRole.USER);
    }
}
