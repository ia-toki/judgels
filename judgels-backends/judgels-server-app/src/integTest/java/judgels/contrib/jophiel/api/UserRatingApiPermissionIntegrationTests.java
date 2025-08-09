package judgels.contrib.jophiel.api;

import java.time.Instant;
import judgels.BaseJudgelsApiIntegrationTests;
import judgels.contrib.jophiel.UserRatingClient;
import judgels.contrib.jophiel.api.user.rating.UserRatingUpdateData;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;

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
