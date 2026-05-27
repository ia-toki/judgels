package tlx.admin.problemset;

import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import tlx.problemset.ProblemSetResource;

@Path("/api/v2/admin/problemsets")
public class ProblemSetAdminResource extends ProblemSetResource {
    @Inject public ProblemSetAdminResource() {}
}
