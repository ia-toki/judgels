package judgels.submission.bundle;

import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import judgels.training.submission.bundle.TrainingItemSubmissionResource;

@Path("/api/v2/submissions/bundle")
public class ItemSubmissionResource extends TrainingItemSubmissionResource {
    @Inject public ItemSubmissionResource() {}
}
