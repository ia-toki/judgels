package judgels.jophiel.api;

import java.util.List;
import judgels.BaseJudgelsApiIntegrationTests;
import judgels.jophiel.UserClient;
import judgels.jophiel.api.user.User;
import judgels.jophiel.api.user.UserData;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;

class UserApiPermissionIntegrationTests extends BaseJudgelsApiIntegrationTests {
    private final UserClient userClient = createClient(UserClient.class);

    @Test
    void create_user() {
        assertPermitted(doCreateUser(adminToken));
        assertForbidden(doCreateUser(userToken));
    }

    @Test
    void get_user() {
        User andi = createUser("andi");
        String andiToken = getToken(andi);

        assertPermitted(getUser(adminToken, andi.getJid()));
        assertPermitted(getUser(andiToken, andi.getJid()));
        assertForbidden(getUser(userToken, andi.getJid()));
    }

    @Test
    void get_users() {
        assertPermitted(getUsers(adminToken));
        assertForbidden(getUsers(userToken));
    }

    @Test
    void export_users() {
        assertPermitted(exportUsers());
    }

    @Test
    void upsert_users() {
        assertPermitted(upsertUsers(adminToken));
        assertForbidden(upsertUsers(userToken));
    }

    private ThrowingCallable doCreateUser(String token) {
        return () -> userClient.createUser(token, new UserData.Builder()
                .username(randomString())
                .password(randomString())
                .email(randomString() + "@domain.com")
                .build());
    }

    private ThrowingCallable getUser(String token, String userJid) {
        return () -> userClient.getUser(token, userJid);
    }

    private ThrowingCallable getUsers(String token) {
        return () -> userClient.getUsers(token);
    }

    private ThrowingCallable exportUsers() {
        return () -> userClient.exportUsers(List.of());
    }

    private ThrowingCallable upsertUsers(String token) {
        return () -> userClient.upsertUsers(token, "country,name,email,username,password\n");
    }
}
