package judgels.admin.contrib.contest.rating;

import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import judgels.contrib.contest.rating.ContestRatingResource;

@Path("/api/v2/admin/contest-rating")
public class ContestRatingAdminResource extends ContestRatingResource {
    @Inject public ContestRatingAdminResource() {}
}
