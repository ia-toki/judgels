package judgels.client;

import feign.RequestLine;
import judgels.api.training.problem.TrainingProblemTagsResponse;

public interface TrainingProblemTagClient {
    @RequestLine("GET /api/v4/training/problems/tags")
    TrainingProblemTagsResponse getProblemTags();
}
