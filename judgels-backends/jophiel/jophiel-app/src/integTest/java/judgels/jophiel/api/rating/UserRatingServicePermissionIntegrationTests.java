package judgels.jophiel.api.rating;

import java.time.Instant;
import judgels.jophiel.api.AbstractServiceIntegrationTests;
import judgels.jophiel.api.user.rating.UserRatingService;
import judgels.jophiel.api.user.rating.UserRatingUpdateData;
import judgels.service.api.actor.AuthHeader;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;

public class UserRatingServicePermissionIntegrationTests extends AbstractServiceIntegrationTests {
    private final UserRatingService userRatingService = createService(UserRatingService.class);

    @Test
    void update_ratings() {
        assertPermitted(updateRatings(adminHeader));
        assertForbidden(updateRatings(userHeader));
    }

    private ThrowingCallable updateRatings(AuthHeader authHeader) {
        return () -> userRatingService.updateRatings(authHeader, new UserRatingUpdateData.Builder()
                .time(Instant.now())
                .eventJid("event-jid")
                .build());
    }
}
