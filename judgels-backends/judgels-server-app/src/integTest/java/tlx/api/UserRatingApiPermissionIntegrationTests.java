package tlx.api;

import java.time.Instant;
import judgels.BaseJudgelsApiIntegrationTests;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import tlx.api.user.rating.UserRatingUpdateData;
import tlx.user.UserRatingAdminClient;

public class UserRatingApiPermissionIntegrationTests extends BaseJudgelsApiIntegrationTests {
    private final UserRatingAdminClient userRatingAdminClient = createClient(UserRatingAdminClient.class);

    @Test
    void update_ratings() {
        assertPermitted(updateRatings(adminToken));
        assertForbidden(updateRatings(userToken));
    }

    private ThrowingCallable updateRatings(String token) {
        return () -> userRatingAdminClient.updateRatings(token, new UserRatingUpdateData.Builder()
                .time(Instant.now())
                .eventJid("event-jid")
                .build());
    }
}
