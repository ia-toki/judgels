package judgels.contest;

import jakarta.inject.Inject;
import jakarta.ws.rs.Path;

@Path("/api/v2/admin/contests")
public class ContestAdminResource extends ContestResource {
    @Inject public ContestAdminResource() {}
}
