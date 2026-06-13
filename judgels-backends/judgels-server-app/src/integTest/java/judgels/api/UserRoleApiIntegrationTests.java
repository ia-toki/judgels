package judgels.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import judgels.BaseJudgelsApiIntegrationTests;
import judgels.api.user.User;
import judgels.api.user.role.UserRole;
import judgels.api.user.role.UserWithRole;
import judgels.user.UserRoleClient;
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
                        .role(new UserRole.Builder().account("ADMIN").problem("ADMIN").contest("ADMIN").training("ADMIN").build())
                        .build());

        userRoleClient.setUserRoles(adminToken, Map.of(
                "admin", new UserRole.Builder().account("ADMIN").problem("ADMIN").contest("ADMIN").training("ADMIN").build(),
                "andi", new UserRole.Builder().contest("ADMIN").build(),
                "budi", new UserRole.Builder().contest("ADMIN").training("ADMIN").build()));

        assertThat(userRoleClient.getUserRoles(adminToken).getData()).containsOnly(
                new UserWithRole.Builder()
                        .userJid(admin.getJid())
                        .role(new UserRole.Builder().account("ADMIN").problem("ADMIN").contest("ADMIN").training("ADMIN").build())
                        .build(),
                new UserWithRole.Builder()
                        .userJid(andi.getJid())
                        .role(new UserRole.Builder().contest("ADMIN").build())
                        .build(),
                new UserWithRole.Builder()
                        .userJid(budi.getJid())
                        .role(new UserRole.Builder().contest("ADMIN").training("ADMIN").build())
                        .build());

        userRoleClient.setUserRoles(adminToken, Map.of(
                "admin", new UserRole.Builder().account("ADMIN").problem("ADMIN").contest("ADMIN").training("ADMIN").build(),
                "budi", new UserRole.Builder().problem("ADMIN").build(),
                "caca", new UserRole.Builder().account("ADMIN").build()));

        assertThat(userRoleClient.getUserRoles(adminToken).getData()).containsOnly(
                new UserWithRole.Builder()
                        .userJid(admin.getJid())
                        .role(new UserRole.Builder().account("ADMIN").problem("ADMIN").contest("ADMIN").training("ADMIN").build())
                        .build(),
                new UserWithRole.Builder()
                        .userJid(budi.getJid())
                        .role(new UserRole.Builder().problem("ADMIN").build())
                        .build(),
                new UserWithRole.Builder()
                        .userJid(caca.getJid())
                        .role(new UserRole.Builder().account("ADMIN").build())
                        .build());
    }
}
