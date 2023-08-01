package judgels.jophiel.api;

import java.util.Map;
import judgels.BaseJudgelsApiIntegrationTests;
import judgels.jophiel.UserRoleClient;
import judgels.jophiel.api.user.role.UserRole;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;

public class UserRoleApiPermissionIntegrationTests extends BaseJudgelsApiIntegrationTests {
    private final UserRoleClient userRoleClient = createClient(UserRoleClient.class);

    @Test
    void get_set_roles() {
        assertPermitted(getSetRoles(superadminToken));
        assertPermitted(getSetRoles(adminToken));
        assertForbidden(getSetRoles(userToken));
    }

    private ThrowingCallable getSetRoles(String token) {
        return callAll(
                () -> userRoleClient.getUserRoles(token),
                () -> userRoleClient.setUserRoles(token, Map.of("admin", new UserRole.Builder().jophiel("ADMIN").build())));
    }
}
