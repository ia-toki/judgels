package judgels.training.problem;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import judgels.api.training.problem.ProblemsResponse;

public interface TrainingProblemClient {
    @RequestLine("GET /api/v4/training/problems")
    @Headers("Authorization: Bearer {token}")
    ProblemsResponse getProblems(@Param("token") String token);
}
