package judgels.api;

import static judgels.api.user.role.UserRole.CONTEST_ADMIN;
import static judgels.api.user.role.UserRole.PROBLEM_ADMIN;
import static judgels.api.user.role.UserRole.PROBLEM_WRITER;
import static judgels.api.user.role.UserRole.SUPERADMIN;
import static judgels.api.user.role.UserRole.SYSTEM_ADMIN;
import static judgels.api.user.role.UserRole.TRAINING_ADMIN;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;
import judgels.BaseJudgelsApiIntegrationTests;
import judgels.api.session.Credentials;
import judgels.api.user.User;
import judgels.api.user.role.UserRole;
import judgels.api.user.role.UserRolesUpsertData;
import judgels.api.user.role.UserWithRoles;
import judgels.client.SessionClient;
import judgels.client.UserRoleClient;
import judgels.client.UserWebClient;
import org.junit.jupiter.api.Test;

public class UserRoleApiIntegrationTests extends BaseJudgelsApiIntegrationTests {
    private static final Set<UserRole> ADMIN_ROLES = Set.of(SYSTEM_ADMIN, PROBLEM_ADMIN, CONTEST_ADMIN, TRAINING_ADMIN);

    private final UserRoleClient userRoleClient = createClient(UserRoleClient.class);
    private final UserWebClient userWebClient = createClient(UserWebClient.class);

    @Test
    void upsert_set_get_roles() {
        String superadminJid = getSuperadminJid();

        User andi = createUser("andi");
        User budi = createUser("budi");

        assertThat(getRoles(adminToken, superadminJid, admin.getJid())).containsExactly(
                userWithRoles(superadminJid, Set.of(SUPERADMIN, SYSTEM_ADMIN, PROBLEM_ADMIN, CONTEST_ADMIN,
                        TRAINING_ADMIN, PROBLEM_WRITER)),
                userWithRoles(admin.getJid(), ADMIN_ROLES));

        // Unknown usernames are left out of the response.
        assertThat(userRoleClient.upsertRoles(adminToken, new UserRolesUpsertData.Builder()
                .addUsernames("andi", "budi", "nonexistent")
                .addRoles(CONTEST_ADMIN)
                .build()).getUpsertedUserProfilesMap()).containsOnlyKeys("andi", "budi");

        // Upserting grants roles on top of the ones already held.
        userRoleClient.upsertRoles(adminToken, new UserRolesUpsertData.Builder()
                .addUsernames("budi")
                .addRoles(TRAINING_ADMIN, PROBLEM_WRITER)
                .build());

        assertThat(getRoles(adminToken, andi.getJid(), budi.getJid())).containsExactlyInAnyOrder(
                userWithRoles(andi.getJid(), Set.of(CONTEST_ADMIN)),
                userWithRoles(budi.getJid(), Set.of(CONTEST_ADMIN, TRAINING_ADMIN, PROBLEM_WRITER)));

        // Setting replaces the roles held, and setting none removes the user.
        userRoleClient.setRoles(adminToken, budi.getJid(), Set.of(PROBLEM_ADMIN));
        userRoleClient.setRoles(adminToken, andi.getJid(), Set.of());

        assertThat(getRoles(adminToken, andi.getJid(), budi.getJid())).containsExactly(
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
    void problem_admin_manages_only_problem_writers() {
        User pia = createUser("pia");
        String piaToken = getToken(pia);
        userRoleClient.setRoles(superadminToken, pia.getJid(), Set.of(PROBLEM_ADMIN));

        User cici = createUser("cici");
        String ciciToken = getToken(cici);
        userRoleClient.setRoles(adminToken, cici.getJid(), Set.of(CONTEST_ADMIN));

        userRoleClient.upsertRoles(piaToken, new UserRolesUpsertData.Builder()
                .addUsernames("cici")
                .addRoles(PROBLEM_WRITER)
                .build());

        assertThat(getRoles(piaToken, pia.getJid(), cici.getJid())).containsExactly(
                userWithRoles(cici.getJid(), Set.of(PROBLEM_WRITER)));

        assertForbidden(() -> userRoleClient.setRoles(piaToken, cici.getJid(), Set.of(CONTEST_ADMIN)));
        assertForbidden(() -> userRoleClient.upsertRoles(piaToken, new UserRolesUpsertData.Builder()
                .addUsernames("cici")
                .addRoles(CONTEST_ADMIN)
                .build()));

        userRoleClient.setRoles(piaToken, cici.getJid(), Set.of());
        assertThat(getRoles(piaToken, cici.getJid())).isEmpty();
        assertThat(userWebClient.getWebConfig(ciciToken).getRoles()).containsExactly(CONTEST_ADMIN);
    }

    @Test
    void system_admin_hides_and_keeps_problem_writer_role() {
        User sam = createUser("sam");
        String samToken = getToken(sam);
        userRoleClient.setRoles(superadminToken, sam.getJid(), Set.of(SYSTEM_ADMIN));

        User dodi = createUser("dodi");
        String dodiToken = getToken(dodi);
        grantProblemWriter(dodi);

        assertThat(getRoles(samToken, dodi.getJid())).isEmpty();
        assertForbidden(() -> userRoleClient.upsertRoles(samToken, new UserRolesUpsertData.Builder()
                .addUsernames("dodi")
                .addRoles(PROBLEM_WRITER)
                .build()));

        userRoleClient.setRoles(samToken, dodi.getJid(), Set.of(CONTEST_ADMIN));
        assertThat(getRoles(samToken, dodi.getJid())).containsExactly(
                userWithRoles(dodi.getJid(), Set.of(CONTEST_ADMIN)));
        assertThat(userWebClient.getWebConfig(dodiToken).getRoles()).containsExactlyInAnyOrder(
                CONTEST_ADMIN, PROBLEM_WRITER);

        userRoleClient.setRoles(samToken, dodi.getJid(), Set.of());
        assertThat(userWebClient.getWebConfig(dodiToken).getRoles()).containsExactly(PROBLEM_WRITER);
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
        assertForbidden(() -> userRoleClient.upsertRoles(adminToken, new UserRolesUpsertData.Builder()
                .addUsernames("superadmin")
                .addRoles(PROBLEM_WRITER)
                .build()));

        assertForbidden(() -> userRoleClient.setRoles(adminToken, admin.getJid(), Set.of(PROBLEM_ADMIN)));
        assertForbidden(() -> userRoleClient.setRoles(adminToken, admin.getJid(), Set.of()));
    }

    private List<UserWithRoles> getRoles(String token, String... userJids) {
        Set<String> jids = Set.of(userJids);
        return userRoleClient.getRoles(token).getData()
                .stream()
                .filter(u -> jids.contains(u.getUserJid()))
                .toList();
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
