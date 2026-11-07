package judgels.api;

import java.util.List;
import judgels.BaseJudgelsApiIntegrationTests;
import judgels.api.user.User;
import judgels.client.ProblemWriterClient;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;

public class ProblemWriterApiPermissionIntegrationTests extends BaseJudgelsApiIntegrationTests {
    private final ProblemWriterClient writerClient = createClient(ProblemWriterClient.class);

    @Test
    void get_set_writers() {
        User writer = createUser("writer");
        grantProblemWriter(writer);

        assertPermitted(getSetWriters(superadminToken));
        assertPermitted(getSetWriters(adminToken));
        assertForbidden(getSetWriters(getToken(writer)));
        assertForbidden(getSetWriters(userToken));
    }

    private ThrowingCallable getSetWriters(String token) {
        return callAll(
                () -> writerClient.getWriters(token),
                () -> writerClient.setWriters(token, List.of()));
    }
}
