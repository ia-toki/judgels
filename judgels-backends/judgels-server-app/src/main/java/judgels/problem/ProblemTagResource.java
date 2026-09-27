package judgels.problem;

import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import judgels.training.problem.TrainingProblemTagResource;

@Path("/api/v2/problems/tags")
public class ProblemTagResource extends TrainingProblemTagResource {
    @Inject public ProblemTagResource() {}
}
