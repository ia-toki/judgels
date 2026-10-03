package judgels.training.problem;

import feign.RequestLine;
import judgels.api.training.problem.ProblemTagsResponse;

public interface TrainingProblemTagClient {
    @RequestLine("GET /api/v4/training/problems/tags")
    ProblemTagsResponse getProblemTags();
}
