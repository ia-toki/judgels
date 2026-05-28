package tlx.problemset;

import jakarta.inject.Inject;
import jakarta.ws.rs.Path;

@Path("/api/v2/admin/problemsets")
public class ProblemSetAdminResource extends ProblemSetResource {
    @Inject public ProblemSetAdminResource() {}
}
