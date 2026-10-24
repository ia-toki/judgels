package judgels.training;

import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import judgels.training.problem.TrainingProblemResource;
import judgels.training.problem.TrainingProblemTagResource;
import judgels.training.stats.TrainingUserStatsResource;
import judgels.training.submission.bundle.TrainingItemSubmissionResource;
import judgels.training.submission.programming.TrainingSubmissionResource;

public final class LegacyTrainingResources {
    private LegacyTrainingResources() {}

    @Path("/api/v2/problems")
    public static class ProblemResource extends TrainingProblemResource {
        @Inject public ProblemResource() {}
    }

    @Path("/api/v2/problems/tags")
    public static class ProblemTagResource extends TrainingProblemTagResource {
        @Inject public ProblemTagResource() {}
    }

    @Path("/api/v2/stats/users")
    public static class UserStatsResource extends TrainingUserStatsResource {
        @Inject public UserStatsResource() {}
    }

    @Path("/api/v2/submissions/programming")
    public static class SubmissionResource extends TrainingSubmissionResource {
        @Inject public SubmissionResource() {}
    }

    @Path("/api/v2/submissions/bundle")
    public static class ItemSubmissionResource extends TrainingItemSubmissionResource {
        @Inject public ItemSubmissionResource() {}
    }
}
