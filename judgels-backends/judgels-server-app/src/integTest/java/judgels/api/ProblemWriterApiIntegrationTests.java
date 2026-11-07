package judgels.api;

import static judgels.api.catalog.problem.ProblemErrors.WRITER_USERNAMES_NOT_FOUND;
import static judgels.api.user.role.UserRole.CONTEST_ADMIN;
import static judgels.api.user.role.UserRole.PROBLEM_WRITER;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import java.util.Set;
import judgels.BaseJudgelsApiIntegrationTests;
import judgels.api.catalog.problem.writer.ProblemWriterData;
import judgels.api.catalog.problem.writer.ProblemWritersResponse;
import judgels.api.user.User;
import judgels.client.ProblemWriterClient;
import judgels.client.UserRoleClient;
import judgels.client.UserWebClient;
import org.junit.jupiter.api.Test;

class ProblemWriterApiIntegrationTests extends BaseJudgelsApiIntegrationTests {
    private final ProblemWriterClient writerClient = createClient(ProblemWriterClient.class);
    private final UserRoleClient userRoleClient = createClient(UserRoleClient.class);
    private final UserWebClient userWebClient = createClient(UserWebClient.class);

    @Test
    void set_writers() {
        User andi = createUser("andi");
        String andiToken = getToken(andi);
        User budi = createUser("budi");

        ProblemWritersResponse response = writerClient.getWriters(adminToken);
        assertThat(response.getData()).isEmpty();
        assertThat(response.getProfilesMap()).isEmpty();

        writerClient.setWriters(adminToken, List.of(writerData("andi"), writerData("budi")));

        response = writerClient.getWriters(adminToken);
        assertThat(response.getData()).containsExactlyInAnyOrder(andi.getJid(), budi.getJid());
        assertThat(response.getProfilesMap()).containsOnlyKeys(andi.getJid(), budi.getJid());

        userRoleClient.setRoles(adminToken, andi.getJid(), Set.of(CONTEST_ADMIN));
        assertThat(userWebClient.getWebConfig(andiToken).getRoles()).containsExactlyInAnyOrder(
                CONTEST_ADMIN, PROBLEM_WRITER);

        writerClient.setWriters(adminToken, List.of(writerData("budi")));

        assertThat(writerClient.getWriters(adminToken).getData()).containsExactly(budi.getJid());
        assertThat(userWebClient.getWebConfig(andiToken).getRoles()).containsExactly(CONTEST_ADMIN);

        assertBadRequest(() -> writerClient.setWriters(adminToken, List.of(writerData("budi"), writerData("ghost"))))
                .hasMessageContaining(WRITER_USERNAMES_NOT_FOUND)
                .hasFieldOrPropertyWithValue("args", Map.of("usernames", "ghost"));
        assertBadRequest(() -> writerClient.setWriters(adminToken, List.of(writerData("budi"), writerData("budi"))));

        assertThat(writerClient.getWriters(adminToken).getData()).containsExactly(budi.getJid());
    }

    private static ProblemWriterData writerData(String username) {
        return new ProblemWriterData.Builder().username(username).build();
    }
}
