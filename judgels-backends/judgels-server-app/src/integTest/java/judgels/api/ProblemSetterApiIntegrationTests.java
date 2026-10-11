package judgels.api;

import static judgels.api.catalog.problem.ProblemErrors.SETTER_USERNAMES_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import judgels.BaseJudgelsApiIntegrationTests;
import judgels.api.catalog.problem.ProblemSetterRole;
import judgels.api.catalog.problem.setter.ProblemSettersResponse;
import judgels.api.user.User;
import judgels.client.ProblemSetterClient;
import judgels.client.ProblemVersionClient;
import org.junit.jupiter.api.Test;

class ProblemSetterApiIntegrationTests extends BaseJudgelsApiIntegrationTests {
    private final ProblemSetterClient setterClient = createClient(ProblemSetterClient.class);
    private final ProblemVersionClient versionClient = createClient(ProblemVersionClient.class);

    @Test
    void set_setters() {
        User writer = createUser("writer");
        User tester = createUser("tester");

        String problemJid = createProblem(adminToken, "problem").getJid();

        // as admin

        ProblemSettersResponse response = setterClient.getSetters(adminToken, problemJid);
        assertThat(response.getData()).isEmpty();
        assertThat(response.getProfilesMap()).isEmpty();

        Map<ProblemSetterRole, List<String>> setterUsernamesMap = Map.of(
                ProblemSetterRole.WRITER, List.of(writer.getUsername()),
                ProblemSetterRole.TESTER, List.of(tester.getUsername(), writer.getUsername()));

        setterClient.setSetters(adminToken, problemJid, setterUsernamesMap);

        response = setterClient.getSetters(adminToken, problemJid);
        assertThat(response.getData()).isEqualTo(Map.of(
                ProblemSetterRole.WRITER, List.of(writer.getJid()),
                ProblemSetterRole.TESTER, List.of(tester.getJid(), writer.getJid())));
        assertThat(response.getProfilesMap()).containsOnlyKeys(writer.getJid(), tester.getJid());

        setterClient.setSetters(adminToken, problemJid, Map.of(ProblemSetterRole.TESTER, List.of(tester.getUsername())));

        response = setterClient.getSetters(adminToken, problemJid);
        assertThat(response.getData()).isEqualTo(Map.of(ProblemSetterRole.TESTER, List.of(tester.getJid())));

        assertBadRequest(() -> setterClient
                .setSetters(adminToken, problemJid, Map.of(ProblemSetterRole.EDITORIALIST, List.of("nonexistent"))))
                .hasMessageContaining(SETTER_USERNAMES_NOT_FOUND);

        // Setters are not part of the problem's files.
        assertThat(versionClient.versionLocalChangesExist(adminToken, problemJid)).isFalse();

        // as user

        assertForbidden(() -> setterClient.getSetters(userToken, problemJid));
        assertForbidden(() -> setterClient.setSetters(userToken, problemJid, setterUsernamesMap));
    }
}
