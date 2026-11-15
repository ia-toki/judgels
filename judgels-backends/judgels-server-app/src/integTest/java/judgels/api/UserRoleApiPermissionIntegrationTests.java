package judgels.api;

import java.util.Set;
import judgels.BaseJudgelsApiIntegrationTests;
import judgels.api.user.User;
import judgels.api.user.role.UserRole;
import judgels.api.user.role.UserRolesUpsertData;
import judgels.client.UserRoleClient;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;

public class UserRoleApiPermissionIntegrationTests extends BaseJudgelsApiIntegrationTests {
    private final UserRoleClient userRoleClient = createClient(UserRoleClient.class);

    @Test
    void get_upsert_set_roles() {
        User problemAdmin = createUser("padmin");
        userRoleClient.setRoles(superadminToken, problemAdmin.getJid(), Set.of(UserRole.PROBLEM_ADMIN));
        User writer = createUser("writer");
        grantProblemWriter(writer);

        assertPermitted(getUpsertSetRoles(superadminToken, UserRole.CONTEST_ADMIN));
        assertPermitted(getUpsertSetRoles(adminToken, UserRole.CONTEST_ADMIN));
        assertPermitted(getUpsertSetRoles(getToken(problemAdmin), UserRole.PROBLEM_WRITER));
        assertForbidden(getUpsertSetRoles(getToken(writer), UserRole.PROBLEM_WRITER));
        assertForbidden(getUpsertSetRoles(userToken, UserRole.CONTEST_ADMIN));
    }

    private ThrowingCallable getUpsertSetRoles(String token, UserRole role) {
        return callAll(
                () -> userRoleClient.getRoles(token),
                () -> userRoleClient.upsertRoles(token, new UserRolesUpsertData.Builder()
                        .addUsernames("user")
                        .addRoles(role)
                        .build()),
                () -> userRoleClient.setRoles(token, user.getJid(), Set.of(role)));
    }
}
