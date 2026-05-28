package tlx.contest.rating;

import jakarta.inject.Inject;
import jakarta.ws.rs.Path;

@Path("/api/v2/admin/contest-rating")
public class ContestRatingAdminResource extends ContestRatingResource {
    @Inject public ContestRatingAdminResource() {}
}
