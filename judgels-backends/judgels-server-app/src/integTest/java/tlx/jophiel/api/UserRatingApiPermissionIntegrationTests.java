package tlx.jophiel.api;

import java.time.Instant;
import judgels.BaseJudgelsApiIntegrationTests;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import tlx.jophiel.UserRatingClient;
import tlx.jophiel.api.user.rating.UserRatingUpdateData;

public class UserRatingApiPermissionIntegrationTests extends BaseJudgelsApiIntegrationTests {
    private final UserRatingClient userRatingClient = createClient(UserRatingClient.class);

    @Test
    void update_ratings() {
        assertPermitted(updateRatings(adminToken));
        assertForbidden(updateRatings(userToken));
    }

    private ThrowingCallable updateRatings(String token) {
        return () -> userRatingClient.updateRatings(token, new UserRatingUpdateData.Builder()
                .time(Instant.now())
                .eventJid("event-jid")
                .build());
    }
}
