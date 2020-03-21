package judgels.uriel.role;

import java.util.Map;
import judgels.jophiel.api.role.UserRole;
import judgels.uriel.AbstractIntegrationTests;

public class AbstractRoleCheckerIntegrationTests extends AbstractIntegrationTests {
    protected void setRoles(RoleChecker roleChecker, Map<String, UserRole> rolesMap) {
        roleChecker.setRoles(rolesMap);
    }
}
