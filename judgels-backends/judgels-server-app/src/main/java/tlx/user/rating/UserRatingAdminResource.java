package tlx.user.rating;

import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import judgels.user.rating.UserRatingResource;

@Path("/api/v2/admin/user-rating")
public class UserRatingAdminResource extends UserRatingResource {
    @Inject public UserRatingAdminResource() {}
}
