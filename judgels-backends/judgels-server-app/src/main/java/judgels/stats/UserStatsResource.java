package judgels.stats;

import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import judgels.training.stats.TrainingUserStatsResource;

@Path("/api/v2/stats/users")
public class UserStatsResource extends TrainingUserStatsResource {
    @Inject public UserStatsResource() {}
}
