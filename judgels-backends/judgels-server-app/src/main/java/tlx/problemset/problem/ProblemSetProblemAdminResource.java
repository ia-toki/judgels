package tlx.problemset.problem;

import jakarta.inject.Inject;
import jakarta.ws.rs.Path;

@Path("/api/v2/admin/problemsets/{problemSetJid}/problems")
public class ProblemSetProblemAdminResource extends ProblemSetProblemResource {
    @Inject public ProblemSetProblemAdminResource() {}
}
