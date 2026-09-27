package judgels.submission.programming;

import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import judgels.training.submission.programming.TrainingSubmissionResource;

@Path("/api/v2/submissions/programming")
public class SubmissionResource extends TrainingSubmissionResource {
    @Inject public SubmissionResource() {}
}
