package judgels.jophiel.api;

import static org.assertj.core.api.Assertions.assertThat;

import judgels.BaseJudgelsApiIntegrationTests;
import judgels.jophiel.MyUserClient;
import judgels.jophiel.SessionClient;
import judgels.jophiel.api.role.JophielRole;
import judgels.jophiel.api.session.Credentials;
import judgels.jophiel.api.user.me.PasswordUpdateData;
import org.junit.jupiter.api.Test;

class MyUserApiIntegrationTests extends BaseJudgelsApiIntegrationTests {
    private final MyUserClient myUserClient = createClient(MyUserClient.class);
    private final SessionClient sessionClient = createClient(SessionClient.class);

    @Test
    void get_myself() {
        assertThat(myUserClient.getMyself(adminToken).getUsername()).isEqualTo("superadmin");
    }

    @Test
    void update_my_password() {
        PasswordUpdateData wrongData = PasswordUpdateData.of("wrongPass", "newPass");
        assertBadRequest(() -> myUserClient.updateMyPassword(userToken, wrongData));
        assertPermitted(() -> sessionClient.logIn(Credentials.of("user", "pass")));
        assertForbidden(() -> sessionClient.logIn(Credentials.of("user", "newPass")));

        PasswordUpdateData correctData = PasswordUpdateData.of("pass", "newPass");
        myUserClient.updateMyPassword(userToken, correctData);

        assertForbidden(() -> sessionClient.logIn(Credentials.of("user", "pass")));
        assertPermitted(() -> sessionClient.logIn(Credentials.of("user", "newPass")));
    }

    @Test
    void get_my_role() {
        assertThat(myUserClient.getMyRole(adminToken).getJophiel()).isEqualTo(JophielRole.SUPERADMIN);
        assertThat(myUserClient.getMyRole(userToken).getJophiel()).isEqualTo(JophielRole.USER);
    }
}
