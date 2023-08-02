package judgels.jophiel.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import judgels.BaseJudgelsApiIntegrationTests;
import judgels.jophiel.UserRoleClient;
import judgels.jophiel.api.user.User;
import judgels.jophiel.api.user.role.UserRole;
import judgels.jophiel.api.user.role.UserWithRole;
import org.junit.jupiter.api.Test;

public class UserRoleApiIntegrationTests extends BaseJudgelsApiIntegrationTests {
    private final UserRoleClient userRoleClient = createClient(UserRoleClient.class);

    @Test
    void set_get_roles() {
        User andi = createUser("andi");
        User budi = createUser("budi");
        User caca = createUser("caca");

        assertThat(userRoleClient.getUserRoles(adminToken).getData()).containsOnly(
                new UserWithRole.Builder()
                        .userJid(admin.getJid())
                        .role(new UserRole.Builder().jophiel("ADMIN").sandalphon("ADMIN").uriel("ADMIN").jerahmeel("ADMIN").build())
                        .build());

        userRoleClient.setUserRoles(adminToken, Map.of(
                "admin", new UserRole.Builder().jophiel("ADMIN").sandalphon("ADMIN").uriel("ADMIN").jerahmeel("ADMIN").build(),
                "andi", new UserRole.Builder().uriel("ADMIN").build(),
                "budi", new UserRole.Builder().uriel("ADMIN").jerahmeel("ADMIN").build()));

        assertThat(userRoleClient.getUserRoles(adminToken).getData()).containsOnly(
                new UserWithRole.Builder()
                        .userJid(admin.getJid())
                        .role(new UserRole.Builder().jophiel("ADMIN").sandalphon("ADMIN").uriel("ADMIN").jerahmeel("ADMIN").build())
                        .build(),
                new UserWithRole.Builder()
                        .userJid(andi.getJid())
                        .role(new UserRole.Builder().uriel("ADMIN").build())
                        .build(),
                new UserWithRole.Builder()
                        .userJid(budi.getJid())
                        .role(new UserRole.Builder().uriel("ADMIN").jerahmeel("ADMIN").build())
                        .build());

        userRoleClient.setUserRoles(adminToken, Map.of(
                "admin", new UserRole.Builder().jophiel("ADMIN").sandalphon("ADMIN").uriel("ADMIN").jerahmeel("ADMIN").build(),
                "budi", new UserRole.Builder().sandalphon("ADMIN").build(),
                "caca", new UserRole.Builder().jophiel("ADMIN").build()));

        assertThat(userRoleClient.getUserRoles(adminToken).getData()).containsOnly(
                new UserWithRole.Builder()
                        .userJid(admin.getJid())
                        .role(new UserRole.Builder().jophiel("ADMIN").sandalphon("ADMIN").uriel("ADMIN").jerahmeel("ADMIN").build())
                        .build(),
                new UserWithRole.Builder()
                        .userJid(budi.getJid())
                        .role(new UserRole.Builder().sandalphon("ADMIN").build())
                        .build(),
                new UserWithRole.Builder()
                        .userJid(caca.getJid())
                        .role(new UserRole.Builder().jophiel("ADMIN").build())
                        .build());
    }
}
