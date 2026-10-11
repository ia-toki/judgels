package judgels.api;

import static org.assertj.core.api.Assertions.assertThat;

import judgels.BaseJudgelsApiIntegrationTests;
import judgels.api.session.Credentials;
import judgels.api.user.User;
import judgels.api.user.role.UserRole;
import judgels.api.user.role.UserRolesUpsertData;
import judgels.api.user.role.UserWithRole;
import judgels.client.SessionClient;
import judgels.client.UserRoleClient;
import org.junit.jupiter.api.Test;

public class UserRoleApiIntegrationTests extends BaseJudgelsApiIntegrationTests {
    private static final UserRole ALL_ROLES =
            new UserRole.Builder().account("ADMIN").problem("ADMIN").contest("ADMIN").training("ADMIN").build();

    private final UserRoleClient userRoleClient = createClient(UserRoleClient.class);

    @Test
    void upsert_set_get_roles() {
        String superadminJid = createClient(SessionClient.class)
                .logIn(Credentials.of("superadmin", "superadmin"))
                .getUserJid();
        UserWithRole superadminRole = userWithRole(superadminJid, new UserRole.Builder()
                .account("SUPERADMIN")
                .problem("ADMIN")
                .contest("ADMIN")
                .training("ADMIN")
                .build());

        User andi = createUser("andi");
        User budi = createUser("budi");

        assertThat(userRoleClient.getRoles(adminToken).getData()).containsExactly(
                superadminRole,
                userWithRole(admin.getJid(), ALL_ROLES));

        // Unknown usernames are left out of the response.
        assertThat(userRoleClient.upsertRoles(adminToken, new UserRolesUpsertData.Builder()
                .addUsernames("andi", "budi", "nonexistent")
                .role(new UserRole.Builder().contest("ADMIN").build())
                .build()).getUpsertedUserProfilesMap()).containsOnlyKeys("andi", "budi");

        // Upserting grants roles on top of the ones already held.
        userRoleClient.upsertRoles(adminToken, new UserRolesUpsertData.Builder()
                .addUsernames("budi")
                .role(new UserRole.Builder().training("ADMIN").build())
                .build());

        assertThat(userRoleClient.getRoles(adminToken).getData()).containsExactlyInAnyOrder(
                superadminRole,
                userWithRole(admin.getJid(), ALL_ROLES),
                userWithRole(andi.getJid(), new UserRole.Builder().contest("ADMIN").build()),
                userWithRole(budi.getJid(), new UserRole.Builder().contest("ADMIN").training("ADMIN").build()));

        // Setting replaces the roles held, and setting none removes the user.
        userRoleClient.setRole(adminToken, budi.getJid(), new UserRole.Builder().problem("ADMIN").build());
        userRoleClient.setRole(adminToken, andi.getJid(), new UserRole.Builder().build());

        assertThat(userRoleClient.getRoles(adminToken).getData()).containsExactly(
                superadminRole,
                userWithRole(admin.getJid(), ALL_ROLES),
                userWithRole(budi.getJid(), new UserRole.Builder().problem("ADMIN").build()));

        assertBadRequest(() -> userRoleClient
                .setRole(adminToken, budi.getJid(), new UserRole.Builder().account("SUPERADMIN").build()));
        assertNotFound(() -> userRoleClient.setRole(adminToken, "nonexistent", ALL_ROLES));
    }

    @Test
    void protects_superadmin_and_own_system_admin_role() {
        String superadminJid = createClient(SessionClient.class)
                .logIn(Credentials.of("superadmin", "superadmin"))
                .getUserJid();

        assertForbidden(() -> userRoleClient.setRole(adminToken, superadminJid, ALL_ROLES));
        assertForbidden(() -> userRoleClient.upsertRoles(adminToken, new UserRolesUpsertData.Builder()
                .addUsernames("superadmin")
                .role(ALL_ROLES)
                .build()));

        assertForbidden(() -> userRoleClient
                .setRole(adminToken, admin.getJid(), new UserRole.Builder().problem("ADMIN").build()));
        assertForbidden(() -> userRoleClient.setRole(adminToken, admin.getJid(), new UserRole.Builder().build()));
    }

    private static UserWithRole userWithRole(String userJid, UserRole role) {
        return new UserWithRole.Builder().userJid(userJid).role(role).build();
    }
}
