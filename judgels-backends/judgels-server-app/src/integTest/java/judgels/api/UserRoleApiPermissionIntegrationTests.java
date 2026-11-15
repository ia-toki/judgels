package judgels.api;

import java.util.Set;
import judgels.BaseJudgelsApiIntegrationTests;
import judgels.api.user.User;
import judgels.api.user.role.UserRole;
import judgels.api.user.role.UserRolesAddData;
import judgels.client.UserRoleClient;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;

public class UserRoleApiPermissionIntegrationTests extends BaseJudgelsApiIntegrationTests {
    private final UserRoleClient userRoleClient = createClient(UserRoleClient.class);

    @Test
    void get_add_set_roles() {
        User problemAdmin = createUser("padmin");
        userRoleClient.setRoles(superadminToken, problemAdmin.getJid(), Set.of(UserRole.PROBLEM_ADMIN));
        User writer = createUser("writer");
        grantProblemWriter(writer);

        assertPermitted(getAddSetRoles(superadminToken, UserRole.CONTEST_ADMIN));
        assertPermitted(getAddSetRoles(adminToken, UserRole.CONTEST_ADMIN));
        assertPermitted(getAddSetRoles(getToken(problemAdmin), UserRole.PROBLEM_WRITER));
        assertForbidden(getAddSetRoles(getToken(writer), UserRole.PROBLEM_WRITER));
        assertForbidden(getAddSetRoles(userToken, UserRole.CONTEST_ADMIN));
    }

    private ThrowingCallable getAddSetRoles(String token, UserRole role) {
        return callAll(
                () -> userRoleClient.getRoles(token),
                () -> userRoleClient.addRoles(token, new UserRolesAddData.Builder()
                        .addUsernames("user")
                        .addRoles(role)
                        .build()),
                () -> userRoleClient.setRoles(token, user.getJid(), Set.of(role)));
    }
}
