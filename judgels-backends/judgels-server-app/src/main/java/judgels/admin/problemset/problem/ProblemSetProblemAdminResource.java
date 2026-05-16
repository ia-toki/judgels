package judgels.admin.problemset.problem;

import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import judgels.problemset.problem.ProblemSetProblemResource;

@Path("/api/v2/admin/problemsets/{problemSetJid}/problems")
public class ProblemSetProblemAdminResource extends ProblemSetProblemResource {
    @Inject public ProblemSetProblemAdminResource() {}
}
