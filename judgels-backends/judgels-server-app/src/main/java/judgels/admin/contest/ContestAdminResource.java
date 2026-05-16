package judgels.admin.contest;

import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import judgels.contest.ContestResource;

@Path("/api/v2/admin/contests")
public class ContestAdminResource extends ContestResource {
    @Inject public ContestAdminResource() {}
}
