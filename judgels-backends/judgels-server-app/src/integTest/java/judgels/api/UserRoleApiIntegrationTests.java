package judgels.api;

import static judgels.api.user.role.UserRole.CONTEST_ADMIN;
import static judgels.api.user.role.UserRole.PROBLEM_ADMIN;
import static judgels.api.user.role.UserRole.SUPERADMIN;
import static judgels.api.user.role.UserRole.SYSTEM_ADMIN;
import static judgels.api.user.role.UserRole.TRAINING_ADMIN;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import judgels.BaseJudgelsApiIntegrationTests;
import judgels.api.session.Credentials;
import judgels.api.user.User;
import judgels.api.user.role.UserRole;
import judgels.api.user.role.UserRolesUpsertData;
import judgels.api.user.role.UserWithRoles;
import judgels.client.SessionClient;
import judgels.client.UserRoleClient;
import org.junit.jupiter.api.Test;

public class UserRoleApiIntegrationTests extends BaseJudgelsApiIntegrationTests {
    private static final Set<UserRole> ADMIN_ROLES = Set.of(SYSTEM_ADMIN, PROBLEM_ADMIN, CONTEST_ADMIN, TRAINING_ADMIN);

    private final UserRoleClient userRoleClient = createClient(UserRoleClient.class);

    @Test
    void upsert_set_get_roles() {
        String superadminJid = getSuperadminJid();
        UserWithRoles superadminRoles = userWithRoles(superadminJid, Set.of(UserRole.values()));

        User andi = createUser("andi");
        User budi = createUser("budi");

        assertThat(userRoleClient.getRoles(adminToken).getData()).containsExactly(
                superadminRoles,
                userWithRoles(admin.getJid(), ADMIN_ROLES));

        // Unknown usernames are left out of the response.
        assertThat(userRoleClient.upsertRoles(adminToken, new UserRolesUpsertData.Builder()
                .addUsernames("andi", "budi", "nonexistent")
                .addRoles(CONTEST_ADMIN)
                .build()).getUpsertedUserProfilesMap()).containsOnlyKeys("andi", "budi");

        // Upserting grants roles on top of the ones already held.
        userRoleClient.upsertRoles(adminToken, new UserRolesUpsertData.Builder()
                .addUsernames("budi")
                .addRoles(TRAINING_ADMIN)
                .build());

        assertThat(userRoleClient.getRoles(adminToken).getData()).containsExactlyInAnyOrder(
                superadminRoles,
                userWithRoles(admin.getJid(), ADMIN_ROLES),
                userWithRoles(andi.getJid(), Set.of(CONTEST_ADMIN)),
                userWithRoles(budi.getJid(), Set.of(CONTEST_ADMIN, TRAINING_ADMIN)));

        // Setting replaces the roles held, and setting none removes the user.
        userRoleClient.setRoles(adminToken, budi.getJid(), Set.of(PROBLEM_ADMIN));
        userRoleClient.setRoles(adminToken, andi.getJid(), Set.of());

        assertThat(userRoleClient.getRoles(adminToken).getData()).containsExactly(
                superadminRoles,
                userWithRoles(admin.getJid(), ADMIN_ROLES),
                userWithRoles(budi.getJid(), Set.of(PROBLEM_ADMIN)));

        assertNotFound(() -> userRoleClient.setRoles(adminToken, "nonexistent", ADMIN_ROLES));
    }

    @Test
    void refuses_superadmin_role() {
        User budi = createUser("budi2");

        assertBadRequest(() -> userRoleClient.setRoles(adminToken, budi.getJid(), Set.of(SUPERADMIN)));
        assertBadRequest(() -> userRoleClient.upsertRoles(adminToken, new UserRolesUpsertData.Builder()
                .addUsernames("budi2")
                .addRoles(SUPERADMIN)
                .build()));
    }

    @Test
    void protects_superadmin_and_own_system_admin_role() {
        String superadminJid = getSuperadminJid();

        assertForbidden(() -> userRoleClient.setRoles(adminToken, superadminJid, ADMIN_ROLES));
        assertForbidden(() -> userRoleClient.setRoles(superadminToken, superadminJid, Set.of()));
        assertForbidden(() -> userRoleClient.upsertRoles(adminToken, new UserRolesUpsertData.Builder()
                .addUsernames("superadmin")
                .addRoles(CONTEST_ADMIN)
                .build()));

        assertForbidden(() -> userRoleClient.setRoles(adminToken, admin.getJid(), Set.of(PROBLEM_ADMIN)));
        assertForbidden(() -> userRoleClient.setRoles(adminToken, admin.getJid(), Set.of()));
    }

    private String getSuperadminJid() {
        return createClient(SessionClient.class)
                .logIn(Credentials.of("superadmin", "superadmin"))
                .getUserJid();
    }

    private static UserWithRoles userWithRoles(String userJid, Set<UserRole> roles) {
        return new UserWithRoles.Builder().userJid(userJid).roles(roles).build();
    }
}
